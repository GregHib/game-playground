package world.gregs.game.playground

import org.openjdk.jmh.annotations.*
import world.gregs.game.playground.pathfinding.bfs.Collisions
import world.gregs.game.playground.pathfinding.bfs.TargetPredicate
import world.gregs.game.playground.pathfinding.bfs.UnsafeBreadthFirstSearch
import world.gregs.game.playground.pathfinding.bfs.unsafe.Unsafe2DIntArray
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
class BreadthFirstSearchBenchmark {
    @Param("32", "64"/*, "104", "128"*/)
    var width = 0

    @Param("32", "64"/*, "104", "128"*/)
    var height = 0

    @Param("0.0"/*, "0.2", "0.4", "0.6"*/)
    var percent = 0.0

    lateinit var collisionData: BitSet
    lateinit var collisions: Collisions
    lateinit var bfs: UnsafeBreadthFirstSearch
    lateinit var target: TargetPredicate

    @Setup
    fun setup() {
        target = object : TargetPredicate {
            override fun reached(x: Int, y: Int, z: Int): Boolean {
                return x == width - 1 && y == height - 1
            }

            override fun distance(x: Int, y: Int, z: Int): Int {
                val deltaX = if (width - 1 > x) width - 1 - x else x - width - 1
                val deltaY = if (height - 1 > y) height - 1 - y else y - height - 1
                return deltaX * deltaX + deltaY * deltaY
            }
        }
        val unsafe = UnsafeBreadthFirstSearch.getUnsafe()
        collisionData = BitSet(width * height)
        for (x in 0 until width) {
            for (y in 0 until height) {
                collisionData[UnsafeBreadthFirstSearch.hash(x, y)] = Random.nextDouble() < percent
            }
        }
        collisions = object : Collisions {
            override fun blocked(fromX: Int, fromY: Int, toX: Int, toY: Int, direction: Int, z: Int): Boolean {
                return collisionData[UnsafeBreadthFirstSearch.hash(toX, toY)]
            }
        }
        bfs = UnsafeBreadthFirstSearch(collisions, unsafe, width, height)
    }

    @TearDown
    fun tearDown() {
        collisionData.clear()
        bfs.clear()
    }

    @Benchmark
    fun benchmark(): Int {
        return bfs.search(target, 0, 0)
    }

    @Benchmark
    fun benchmarkPartial(): Int {
        return bfs.searchPartial(target, 0, 0)
    }
}