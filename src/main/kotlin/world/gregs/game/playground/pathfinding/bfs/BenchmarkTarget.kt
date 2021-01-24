package world.gregs.game.playground.pathfinding.bfs

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.random.Random
import kotlin.system.measureNanoTime

object BenchmarkTarget {
    @JvmStatic
    fun main(args: Array<String>) {
        val columns = 128
        val rows = 128
        val percent = 0.0
        val data = File("C:\\Users\\Greg\\IdeaProjects\\game-playground\\benchmarks\\src\\jmh\\resources\\test2.dat").readBytes()
        val distances = IntArray(columns * rows) { -1 }
        val collision = Array(columns) { x -> Array(rows) { y -> data[x + (y * rows)] == 1.toByte() } }

        fun index(x: Int, y: Int) = x + (y * columns)

        val startX = 64
        val startY = 64

        val targetX = 127
        val targetY = 127

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
            distances.fill(-1)
            writeIndex = 0
            readIndex = 0
            queue[writeIndex++] = hash(startX, startY)
            distances[index(startX, startY)] = 0
        }

        fun check(parentX: Int, parentY: Int, x: Int, y: Int) {
            if (collision.getOrNull(parentX + x)?.getOrNull(parentY + y) == false && distances[index(parentX + x, parentY + y)] == -1) {
                distances[index(parentX + x, parentY + y)] = distances[index(parentX, parentY)] + 1
                queue[writeIndex++] = hash(parentX + x, parentY + y)
            }
        }

        fun bfs(): Long {
            return measureNanoTime {
                reset()
                while (readIndex < writeIndex) {
                    val parent = queue[readIndex++]
                    val parentX = getX(parent)
                    val parentY = getY(parent)
                    if (parentX == targetX && parentY == targetY) {
                        break
                    }
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
        bfs()

        for (y in columns - 1 downTo 0) {
            for (x in 0 until rows) {
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
//    randomise()
            total += bfs()
        }
        println("BFS took $total avg ${total / times}")
//        BFS took 6134588500 avg 613458
    }
}