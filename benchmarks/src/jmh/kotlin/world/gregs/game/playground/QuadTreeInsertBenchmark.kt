package world.gregs.game.playground

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import world.gregs.game.playground.spacial.quadtree.QuadTree
import world.gregs.game.playground.spacial.quadtree.point.PointQuadTree
import java.awt.Point
import java.awt.Rectangle
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
class QuadTreeInsertBenchmark {
    @Param("10", "100", "1000")
    var entities = 0

    lateinit var quadTree: QuadTree
    lateinit var testData: Array<Point>

    @Setup
    fun setup() {
        val bounds = Rectangle(0, 0, 256, 256)
        quadTree = PointQuadTree(bounds, 4)
        val list = mutableListOf<Point>()
        for(i in 0 until entities) {
            list.add(Point(Random.nextInt(bounds.x, bounds.x + bounds.width), Random.nextInt(bounds.y, bounds.y + bounds.height)))
        }
        testData = list.toTypedArray()
    }

    @Benchmark
    fun benchmark(bh: Blackhole): QuadTree {
        for(point in testData) {
            bh.consume(quadTree.insert(point))
        }
        return quadTree
    }
}