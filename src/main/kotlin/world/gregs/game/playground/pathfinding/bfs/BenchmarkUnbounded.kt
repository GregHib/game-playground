package world.gregs.game.playground.pathfinding.bfs

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.measureNanoTime

object BenchmarkUnbounded {
    @JvmStatic
    fun main(args: Array<String>) {
        val columns = 130
        val rows = 130
        val percent = 0.0
        val data = File("C:\\Users\\Greg\\IdeaProjects\\game-playground\\benchmarks\\src\\jmh\\resources\\test2.dat").readBytes()
        val distances = IntArray(columns * rows) { -1 }

        val collision = Array<Boolean?>(columns * rows) { null }

        fun index(x: Int, y: Int) = (x + 1) + ((y + 1) * columns)

        // By giving the clipped map a boarder we seemingly get a performance increase by removing the bound checks,
        // However that extra time isn't saved as extra work has to be done to copy the data to the clipping in the first place.
        for (x in 0 until 128) {
            for (y in 0 until 128) {
                collision[index(x, y)] = data[x + (y * 128)] == 1.toByte()
            }
        }

        val startX = 64
        val startY = 64

        collision[index(startX, startY)] = false

        val queue = IntArray(columns * rows) { -1 }

        var writeIndex = 0
        var readIndex = 0

        fun reset() {
            distances.fill(-1)
            writeIndex = 0
            readIndex = 0
            queue[writeIndex++] = index(startX, startY)
            distances[index(startX, startY)] = 0
        }

        fun check(parent: Int, offset: Int) {
            if (collision.getOrNull(parent + offset) == false && distances[parent + offset] == -1) {
                distances[parent + offset] = distances[parent] + 1
                queue[writeIndex++] = parent + offset
            }
        }

        fun bfs(): Long {
            return measureNanoTime {
                reset()
                while (readIndex < writeIndex) {
                    val parent = queue[readIndex++]
                    check(parent, -1)
                    check(parent, 1)
                    check(parent, -130)
                    check(parent, 130)
                    check(parent, -131)
                    check(parent, -129)
                    check(parent, 129)
                    check(parent, 131)
                }
            }
        }
        // Warm-up
        bfs()

        for (y in 128 - 1 downTo 1) {
            for (x in 1 until 128) {
                val distance = if (distances[index(x, y)] != -1) distances[index(x, y)] else 0
                print("$distance${if (distance in 0..9) " " else ""} ")
            }
            println()
        }

        runBlocking {
            repeat(20) {
                delay(it * 250L)
                repeat(it) {
                    println("${bfs()}ns")
                }
            }
        }

        val times = 10000
        var total = 0L
        repeat(times) {
            total += bfs()
        }
        println("BFS took $total avg ${total / times}")
//        BFS took 4155712899 avg 415571
    }
}