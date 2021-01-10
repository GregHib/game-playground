package world.gregs.game.playground

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import world.gregs.game.playground.pathfinding.bfs.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.toPath
import kotlin.random.Random

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 6, timeUnit = TimeUnit.SECONDS)
class BreadthFirstSearchBenchmark {
    @Param(/*"32", "64", "104", */"128")
    var size = 0

    @Param("0", "1", "2", "3", "4", "5")
    var test = 0

    lateinit var collisionData: BitSet
    lateinit var collisions: Collisions
    lateinit var bfs: UnsafeBreadthFirstSearch
    lateinit var target: TargetPredicate

    @Setup
    fun setup() {
        target = object : TargetPredicate {
            override val x: Int = -1
            override val y: Int = -1

            override fun reached(x: Int, y: Int, z: Int) = false

            override fun distance(x: Int, y: Int, z: Int) = -1
        }

        val data = BreadthFirstSearchBenchmark::class.java.getResourceAsStream("/test$test.dat").readAllBytes()
        collisionData = BitSet(size * size)
        for (x in 0 until size) {
            for (y in 0 until size) {
                collisionData[UnsafeBreadthFirstSearch.hash(x, y)] = data[x + (y * size)] == 1.toByte()
            }
        }
        collisions = object : Collisions {
            override fun blocked(fromX: Int, fromY: Int, toX: Int, toY: Int, direction: Int, z: Int): Boolean {
                return collisionData[UnsafeBreadthFirstSearch.hash(toX, toY)]
            }

            override fun blocked(from: Int, to: Int, direction: Int, z: Int): Boolean {
                return collisionData[to]
            }
        }
        bfs = UnsafeBreadthFirstSearch(collisions, size, size)
    }

    @TearDown
    fun tearDown() {
        bfs.free()
    }

    @Benchmark
    fun search(): Int {
        return bfs.search(target, 0, 0, -1)
    }

    @Benchmark
    fun searchInside(): Int {
        return bfs.searchInside(target, 0, 0, -1)
    }

    @Benchmark
    fun searchPartial(bh: Blackhole) {
        bh.consume(bfs.search(target, 0, 0, -1))
        bh.consume(bfs.searchPartial(target, -1, 10))
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            repeat(6) {
                createMap(it)
            }
        }

        private fun createMap(index: Int) {
            val percent = index / 10.0
            val file = File("./src/jmh/resources/test$index.dat")
            val size = 128
            val array = ByteArray(size * size)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    array[x + (y * size)] = if (Random.nextDouble() < percent) 1 else 0
                }
            }
            file.writeBytes(array)
        }
    }
}