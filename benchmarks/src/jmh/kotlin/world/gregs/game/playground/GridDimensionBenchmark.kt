package world.gregs.game.playground

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
class GridDimensionBenchmark {
    @Param("10000", "50000", "100000")
    var size = 0

    lateinit var multiGrid: Array<Array<Byte>>
    lateinit var grid: ByteArray

    var readX: Int = 0
    var readY: Int = 0

    @Setup
    fun setup() {
        multiGrid = Array(size) { Array(size) { 0.toByte() } }
        grid = ByteArray(size * size)
        for(x in 0 until size) {
            for(y in 0 until size) {
                val random = Random.nextInt().toByte()
                multiGrid[x][y] = random
                grid[x + y * size] = random
            }
        }
        readX = Random.nextInt(0, size - 1)
        readY = Random.nextInt(0, size - 1)
    }

    @Benchmark
    fun benchmarkMulti(): Byte {
        return multiGrid[readX][readY]
    }

    @Benchmark
    fun benchmark(): Byte {
        return grid[readX + readY * size]
    }
}