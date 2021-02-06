package world.gregs.game.playground.pathfinding.bfs

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.random.Random
import kotlin.system.measureNanoTime

object BenchmarkCounted1dCollision {
    @JvmStatic
    fun main(args: Array<String>) {
        val columns = 128
        val rows = 128
        val percent = 0.0
        val data = File("C:\\Users\\Greg\\IdeaProjects\\game-playground\\benchmarks\\src\\jmh\\resources\\test2.dat").readBytes()

        /*
            This impl keeps track of whether a tile has been visited this turn and packs it in with the distance value
            This means the distance value for the frontier doesn't have to be reset before each call,
            on a map of this size it only makes a 1% improvement, but it'd have a bigger impact on larger maps
         */
        fun pack(x: Int, y: Int) = y or (x shl 16)

        val frontier = IntArray(columns * rows) { pack(0, 0) }
        val collision = Array(columns * rows) { false }


        fun index(x: Int, y: Int) = x + (y * columns)

        for (x in 0 until columns) {
            for (y in 0 until rows) {
//                collision[index(x, y)] = data[x + (y * rows)] == 1.toByte()
            }
        }

        val startX = 64
        val startY = 64

        collision[index(startX, startY)] = false

        val queue = IntArray(columns * rows) { -1 }

        var writeIndex = 0
        var readIndex = 0
        var visit = 0

        fun randomise() {
            for (x in 0 until columns) {
                for (y in 0 until rows) {
                    collision[index(x, y)] = Random.nextDouble() < percent
                }
            }
        }

        fun getX(value: Int) = value shr 16

        fun getDistance(value: Int) = getX(value)

        fun getY(value: Int) = value and 0xffff

        fun getVisit(value: Int) = getY(value)

        fun reset() {
            visit++
            writeIndex = 0
            readIndex = 0
            queue[writeIndex++] = pack(startX, startY)
            frontier[index(startX, startY)] = pack(0, visit)
        }

        fun check(parentX: Int, parentY: Int, x: Int, y: Int) {
            if (collision.getOrNull(index(parentX + x, parentY + y)) == false && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit)
                queue[writeIndex++] = pack(parentX + x, parentY + y)
            }
        }

        fun bfs(): Long {
            return measureNanoTime {
                reset()
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
        bfs()

        for (y in columns - 1 downTo 0) {
            for (x in 0 until rows) {
                val distance = if (getY(frontier[index(x, y)]) == visit) getX(frontier[index(x, y)]) else 0
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
        println(visit)
//        BFS took 6134588500 avg 613458
    }
}