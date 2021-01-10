package world.gregs.game.playground.pathfinding.bfs

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.random.Random
import kotlin.system.measureNanoTime

object Benchmark {
    @JvmStatic
    fun main(args: Array<String>) {
        val columns = 128
        val rows = 128
        val percent = 0.0
        val data = File("C:\\Users\\Greg\\IdeaProjects\\game-playground\\benchmarks\\src\\jmh\\resources\\test2.dat").readBytes()
        val distances = Array(columns) { Array(rows) { -1.0 } }
        val collision = Array(columns) { x -> Array(rows) { y -> data[x + (y * rows)] == 1.toByte() } }

        val startX = 64
        val startY = 64

        collision[startX][startY] = false

        val queue = IntArray(columns * rows) { -1 }

        var writeIndex = 0
        var readIndex = 0

        fun randomise() {
            for (x in 0 until columns) {
                for (y in 0 until rows) {
                    collision[x][y] = Random.nextDouble() < percent
                }
            }
        }

        fun hash(x: Int, y: Int) = y or (x shl 16)

        fun getX(hash: Int) = hash shr 16

        fun getY(hash: Int) = hash and 0xffff

        fun reset() {
            distances.forEach {
                it.fill(-1.0)
            }
            writeIndex = 0
            readIndex = 0
            queue.fill(-1)

            queue[writeIndex++] = hash(startX, startY)
            distances[startX][startY] = 0.0
        }

        fun check(parentX: Int, parentY: Int, x: Int, y: Int) {
            if (collision.getOrNull(parentX + x)?.getOrNull(parentY + y) == false && distances[parentX + x][parentY + y] == -1.0) {
                distances[parentX + x][parentY + y] = distances[parentX][parentY] + 1.0
                queue[writeIndex++] = hash(parentX + x, parentY + y)
            }
        }

        fun bfs(): Long {
            return measureNanoTime {
                while (readIndex < writeIndex) {
                    val parent = queue[readIndex++]
                    val parentX = getX(parent)
                    val parentY = getY(parent)
                    check(parentX, parentY, -1, 0)
                    check(parentX, parentY, 1, 0)
                    check(parentX, parentY, 0, -1)
                    check(parentX, parentY, 0, 1)
                    check(parentX, parentY, -1, -1)
                    check(parentX, parentY, 1, -1)
                    check(parentX, parentY, -1, 1)
                    check(parentX, parentY, 1, 1)
                }
            }
        }

// Warm-up
        reset()
        bfs()

        runBlocking {
            repeat(20) {
                delay(it * 250L)
                repeat(it) {
                    Benchmark2.reset()
                    println("${Benchmark2.bfs()}ns")
                }
            }
        }

        val times = 2000
        var total = 0L
        repeat(times) {
//    randomise()
            reset()
            total += bfs()
        }
        println("BFS took $total avg ${total / times}")
    }
}