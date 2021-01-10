package world.gregs.game.playground.pathfinding.bfs.unsafe

import sun.misc.Unsafe

/**
 * A first in first out circular queue which overrides previously read values once [length] is reached
 */
class UnsafeFifoCircularQueue(
    unsafe: Unsafe,
    private val length: Int
) {

    private val array = UnsafeIntArray(unsafe, length)
    private var writeIndex: Int = 0
    private var readIndex: Int = 0
    private var writeLaps: Int = 0
    private var readLaps: Int = 0

    fun isNotEmpty() = readIndex < writeIndex || readLaps + 1 == writeLaps

    val size: Int
        get() = writeIndex - readIndex

    fun add(value: Int) {
        if (writeIndex >= length) {
            writeIndex = 0
            writeLaps++
        }
        array[writeIndex++] = value
    }

    fun peek(): Int {
        return array[readIndex]
    }

    fun poll(): Int {
        if (readIndex >= length) {
            readIndex = 0
            readLaps++
        }
        return array[readIndex++]
    }

    operator fun set(index: Int, value: Int) {
        array[index] = value
    }

    operator fun get(index: Int): Int {
        return array[index]
    }

    fun setDefault(value: Int) {
        array.setDefault(value)
    }

    fun reset() {
        readIndex = 0
        writeIndex = 0
    }

    /**
     * Frees allocated memory
     */
    fun free() {
        array.free()
    }

}