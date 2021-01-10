package world.gregs.game.playground.pathfinding.bfs

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import sun.misc.Unsafe
import java.io.File
import java.lang.reflect.Field
import java.util.*
import kotlin.random.Random
import kotlin.system.measureNanoTime

object Benchmark2 {
    const val columns = 128
    const val rows = 128
    const val colbyrows = columns * rows
    const val percent = 0.3F
    const val fill = -1
    const val fillF = -1L

    object distances {
        const val bytes = colbyrows * 8L * 2

        val address = unsafe.allocateMemory(bytes)
        fun index(x: Int, y: Int) = ((x shl 6) or y) * 8

        operator fun set(x: Int, y: Int, value: Long) {
            unsafe.putLong(address + index(x, y), value)
        }
        operator fun get(x: Int, y: Int): Long {
            return unsafe.getLong(address + index(x, y))
        }

        val fillAddress = unsafe.allocateMemory(bytes)

        init {
            for (x in 0..columns-1) for (y in 0..rows-1) unsafe.putLong(fillAddress + index(x, y), fillF)
            fill()
        }

        fun fill() = unsafe.copyMemory(fillAddress, address, bytes)
    }

    object collision {
        fun index(x: Int, y: Int) = ((x + 1) shl 7) or (y + 1)

        val bitset = BitSet(colbyrows)

        operator fun set(x: Int, y: Int, value: Boolean) = bitset.set(index(x, y), value)
        operator fun get(x: Int, y: Int) = bitset.get(index(x, y))
    }

    const val startX = 64
    const val startY = 64
    const val start = 0

    val unsafe: Unsafe

    init {
        val f: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
        f.isAccessible = true
        unsafe = f.get(null) as Unsafe

        collision[startX, startY] = false
    }

    object queue {
        const val bytes = colbyrows * 8L
        val address = unsafe.allocateMemory(bytes)

        fun index(i: Int) = i shl 2

        operator fun set(i: Int, x: Int, y: Int) = unsafe.putLong(address + index(i), xy(x, y))
        operator fun get(i: Int) = unsafe.getLong(address + index(i))

        fun xy(x: Int, y: Int) = ((x.toLong() and 0xFFFFFFFF) shl 32) or (y.toLong() and 0xFFFFFFFF)

        fun x(value: Long) = ((value ushr 32) and 0xFFFFFFFF).toInt()
        fun y(value: Long) = (value and 0xFFFFFFFF).toInt()

        val fillAddress = unsafe.allocateMemory(bytes)

        init {
            val negative1 = xy(-1, -1)
            for (i in 0..colbyrows-1) {
                unsafe.putLong(fillAddress + index(i), negative1)
            }
        }

        fun fill() = unsafe.copyMemory(fillAddress, address, bytes)
    }

    var writeIndex = 0
    var readIndex = 0

    fun reset() {
        distances.fill()

        writeIndex = 0
        readIndex = 0
        queue.fill()

        queue[writeIndex, writeIndex++] = start

        distances[startX, startY] = 0L
    }

    fun check(parentX: Int, parentY: Int, x: Int, y: Int) {
        val px = parentX + x
        val py = parentY + y
        if (!collision[px, py] && distances[px, py] == -1L) {
            distances[px, py] = distances[parentX, parentY] + 1L
            queue[writeIndex++, px] = py
        }
    }

    fun bfs(): Long {
        return measureNanoTime {
            while (readIndex < writeIndex) {
                val parent = queue[readIndex++]
                val parentX = queue.x(parent)
                val parentY = queue.y(parent)
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

    @JvmStatic
    fun main(args: Array<String>) {
        // Warm-up
        setup()

        val times = 2000
        var total = 0L
        val size = 128
        val data = File("./benchmarks/src/jmh/resources/test2.dat").readBytes()
        for (x in 0 until size) {
            for (y in 0 until size) {
                collision[x, y] = data[x + (y * size)] == 1.toByte()
            }
        }


        runBlocking {
            repeat(20) {
                delay(it * 250L)
                repeat(it) {
                    reset()
                    println("${bfs()}ns")
                }
            }
        }

        repeat(times) {
            reset()
            total += bfs()
        }
        println("BFS took $total avg ${total / times}")
    }

    @JvmStatic
    fun setup() {
        reset()
        bfs()
    }
}