package world.gregs.game.playground.pathfinding.bfs

import it.unimi.dsi.fastutil.ints.IntArrayPriorityQueue
import it.unimi.dsi.fastutil.ints.IntHeapPriorityQueue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import world.gregs.game.playground.euclidean
import java.io.File
import kotlin.math.abs
import kotlin.random.Random
import kotlin.system.measureNanoTime

object BenchmarkTargetPriority {
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

        /*
            Best first search, expands nodes by manhattan distance
         */
        val queue = IntHeapPriorityQueue(columns * rows)

        fun randomise() {
            for (x in 0 until columns) {
                for (y in 0 until rows) {
                    collision[x][y] = Random.nextDouble() < percent
                }
            }
        }

        fun manhattan(x1: Int, y1: Int, x2: Int, y2: Int) = abs(x1 - x2) + abs(y1 - y2)

        fun pack(distance: Int, x: Int, y: Int) = y or (x shl 12) or (distance shl 24)

        fun getDistance(hash: Int) = hash shr 24 and 0xff

        fun getX(hash: Int) = hash shr 12 and 0xfff

        fun getY(hash: Int) = hash and 0xfff

        fun reset() {
            distances.fill(-1)
            queue.enqueue(pack(manhattan(startX, startY, targetX, targetY), startX, startY))
            distances[index(startX, startY)] = 0
        }

        fun check(parentX: Int, parentY: Int, x: Int, y: Int) {
            if (collision.getOrNull(parentX + x)?.getOrNull(parentY + y) == false && distances[index(parentX + x, parentY + y)] == -1) {
                distances[index(parentX + x, parentY + y)] = distances[index(parentX, parentY)] + 1
                queue.enqueue(pack(manhattan(parentX + x, parentY + y, targetX, targetY), parentX + x, parentY + y))
            }
        }

        fun bfs(): Long {
            return measureNanoTime {
                reset()
                while (!queue.isEmpty) {
                    val parent = queue.dequeueInt()
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