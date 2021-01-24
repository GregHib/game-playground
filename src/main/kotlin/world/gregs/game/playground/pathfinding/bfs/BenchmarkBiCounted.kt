package world.gregs.game.playground.pathfinding.bfs

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.random.Random
import kotlin.system.measureNanoTime

object BenchmarkBiCounted {
    @JvmStatic
    fun main(args: Array<String>) {
        val columns = 128
        val rows = 128
        val percent = 0.0
        val data = File("C:\\Users\\Greg\\IdeaProjects\\game-playground\\benchmarks\\src\\jmh\\resources\\test2.dat").readBytes()

        /*
            Bidirectional search, 55% improvement over standard counted
         */
        fun pack(x: Int, y: Int, int: Int) = y or (x shl 14) or (int shl 28)
        fun pack(x: Int, y: Int) = y or (x shl 16)

        val frontier = IntArray(columns * rows) { pack(0, 0) }
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
        var visit = 0

        fun randomise() {
            for (x in 0 until columns) {
                for (y in 0 until rows) {
                    collision[x][y] = Random.nextDouble() < percent
                }
            }
        }

        fun getX(value: Int) = value shr 14 and 0x3fff

        fun getY(value: Int) = value and 0x3fff

        fun getInt(value: Int) = value shr 28 and 0x7

        fun getDistance(value: Int) = value shr 16

        fun getVisit(value: Int) = value and 0xffff

        fun reset() {
            visit += 2
            writeIndex = 0
            readIndex = 0
            queue[writeIndex++] = pack(startX, startY, 0)
            queue[writeIndex++] = pack(targetX, targetY, 1)
            frontier[index(startX, startY)] = pack(0, visit)
            frontier[index(targetX, targetY)] = pack(999, visit + 1)
        }

        fun check(parentX: Int, parentY: Int, x: Int, y: Int, type: Int) {
            if (collision.getOrNull(parentX + x)?.getOrNull(parentY + y) == false) {
                if (getVisit(frontier[index(parentX + x, parentY + y)]) != visit + type) {
                    frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + if (type == 1) -1 else 1, visit + type)
                    queue[writeIndex++] = pack(parentX + x, parentY + y, type)
                }
            }
        }

        fun bfs(): Long {
            return measureNanoTime {
                reset()
                while (readIndex < writeIndex) {
                    val parent = queue[readIndex++]
                    val parentX = getX(parent)
                    val parentY = getY(parent)
                    val type = getInt(parent)
                    if (getVisit(frontier[index(parentX, parentY)]) == visit + if (type == 1) 0 else 1) {
                        break
                    }
                    check(parentX, parentY, -1, 0, type)
                    check(parentX, parentY, 1, 0, type)
                    check(parentX, parentY, 0, -1, type)
                    check(parentX, parentY, 0, 1, type)
                    check(parentX, parentY, -1, -1, type)
                    check(parentX, parentY, 1, -1, type)
                    check(parentX, parentY, -1, 1, type)
                    check(parentX, parentY, 1, 1, type)
                }
            }
        }

        // Warm-up
        bfs()

        for (y in columns - 1 downTo 0) {
            for (x in 0 until rows) {
                val distance = if (getVisit(frontier[index(x, y)]) == visit || getVisit(frontier[index(x, y)]) == visit + 1) getDistance(frontier[index(x, y)]) else 0
                print("$distance${if (distance in 10..99) " " else if (distance in 0..9) "  " else ""} ")
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
        println(visit)
//        BFS took 2680479907 avg 268047
    }
}