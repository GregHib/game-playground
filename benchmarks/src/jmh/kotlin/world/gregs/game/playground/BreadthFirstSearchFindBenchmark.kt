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
class BreadthFirstSearchFindBenchmark {
    @Param(/*"32", "64", "104", */"128")
    var size = 0

    @Param("0", "1", "2", "3", "4", "5")
    var test = 0

    @Param("5", "15"/*, "75", "100", "125"*/)
    var targetDistance = 0

    @Param("50", "80"/*, "90", "100", "110"*/, "120")
    var maxPathLength = 0

    lateinit var collisionData: BitSet
    lateinit var collisions: Collisions
    lateinit var bfs: UnsafeBreadthFirstSearch
    lateinit var target: TargetPredicate

    @Setup
    fun setup() {
        target = object : TargetPredicate {
            override val x: Int = targetDistance
            override val y: Int = targetDistance

            override fun reached(x: Int, y: Int, z: Int) = x == targetDistance && y == targetDistance

            override fun distance(x: Int, y: Int, z: Int): Int {
                val deltaX = when {
                    targetDistance > x -> targetDistance - x
                    targetDistance + 1 <= x -> x - (targetDistance + 1) + 1
                    else -> 0
                }
                val deltaY = when {
                    targetDistance > y -> targetDistance - y
                    targetDistance + 1 <= y -> y - (targetDistance + 1) + 1
                    else -> 0
                }
                return deltaX * deltaX + deltaY * deltaY
            }
        }

        val data = BreadthFirstSearchFindBenchmark::class.java.getResourceAsStream("/test$test.dat").readAllBytes()
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
    fun find(bh: Blackhole) {
        bfs.find(target, 0, 0, -1, maxPathLength) {
            bh.consume(it)
        }
    }

    @Benchmark
    fun findPartial(bh: Blackhole) {
        bfs.findPartial(target, 0, 0, -1, maxPathLength) {
            bh.consume(it)
        }
    }
}