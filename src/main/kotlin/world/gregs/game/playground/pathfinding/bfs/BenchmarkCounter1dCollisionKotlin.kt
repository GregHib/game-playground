package world.gregs.game.playground.pathfinding.bfs

import world.gregs.game.playground.pathfinding.bfs.BenchmarkCounter1dCollisionKotlin
import java.util.*
import kotlin.jvm.JvmStatic
import kotlin.system.measureNanoTime

class BenchmarkCounter1dCollisionKotlin {
    var visit = 0
    var frontier = IntArray(columns * rows)
    var collision = BooleanArray(columns * rows)
    var queue = IntArray(columns * rows)
    fun getX(value: Int): Int {
        return value shr 16
    }

    fun getY(value: Int): Int {
        return value and 0xffff
    }

    fun getDistance(value: Int): Int {
        return value shr 16
    }

    fun getVisit(value: Int): Int {
        return value and 0xffff
    }

    fun bfs() {
        visit++
        var writeIndex = 0
        var readIndex = 0
        queue[writeIndex++] = pack(startX, startY)
        frontier[index(startX, startY)] = pack(0, visit)
        while (readIndex < writeIndex) {
            val parent = queue[readIndex]
            readIndex++
            val parentX = getX(parent)
            val parentY = getY(parent)
            var x = -1
            var y = 0
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit)
                queue[writeIndex] = pack(parentX + x, parentY + y)
                writeIndex += 1
            }
            x = 1
            y = 0
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit)
                queue[writeIndex] = pack(parentX + x, parentY + y)
                writeIndex += 1
            }
            x = 0
            y = -1
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit)
                queue[writeIndex] = pack(parentX + x, parentY + y)
                writeIndex += 1
            }
            x = 0
            y = 1
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit)
                queue[writeIndex] = pack(parentX + x, parentY + y)
                writeIndex += 1
            }
            x = -1
            y = -1
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit)
                queue[writeIndex] = pack(parentX + x, parentY + y)
                writeIndex += 1
            }
            x = 1
            y = -1
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit)
                queue[writeIndex] = pack(parentX + x, parentY + y)
                writeIndex += 1
            }
            x = -1
            y = 1
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit)
                queue[writeIndex] = pack(parentX + x, parentY + y)
                writeIndex += 1
            }
            x = 1
            y = 1
            if (parentX + x >= 0 && parentX + x < columns && parentY + y >= 0 && parentY + y < rows && !collision[index(parentX + x, parentY + y)] && getVisit(frontier[index(parentX + x, parentY + y)]) != visit) {
                frontier[index(parentX + x, parentY + y)] = pack(getDistance(frontier[index(parentX, parentY)]) + 1, visit)
                queue[writeIndex] = pack(parentX + x, parentY + y)
                writeIndex += 1
            }
        }
    }

    companion object {
        const val columns = 128
        const val rows = 128
        const val startX = 64
        const val startY = 64
        fun pack(x: Int, y: Int): Int {
            return y or (x shl 16)
        }

        fun index(x: Int, y: Int): Int {
            return x + y * 128
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val bfs = BenchmarkCounter1dCollisionKotlin()
            // Warmup
            for (i in 0..19) {
                println(measureNanoTime {
                    bfs.bfs()
                })
            }
            val count = 1000
            val start = System.nanoTime()
            for (i in 0 until count) {
                bfs.bfs()
            }
            val nano = System.nanoTime() - start
            println("Took " + nano + "ns total " + nano / count + "ns avg")

            /*for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                System.out.print(bfs.getVisit(bfs.frontier[index(x, y)]) + " ");
            }
        }*/
        }
    }

    init {
        Arrays.fill(queue, -1)
    }
}