package world.gregs.game.playground.pathfinding.bfs.unsafe

import sun.misc.Unsafe

class UnsafeIntArray(
    private val unsafe: Unsafe,
    private val length: Int
) {
    private val bytes = length * 4L
    private val address = unsafe.allocateMemory(bytes)
    private val resetAddress = unsafe.allocateMemory(bytes + 4L)

    private fun offset(value: Int): Long = value * 4L

    operator fun set(index: Int, value: Int) {
        if (index >= length || index < 0) {
            throw IndexOutOfBoundsException("Index $index is not within bounds: $length.")
        }
        unsafe.putInt(address + offset(index), value)
    }

    operator fun get(index: Int): Int {
        if (index >= length || index < 0) {
            return unsafe.getInt(resetAddress + bytes)
        }
        return unsafe.getInt(address + offset(index))
    }

    fun setDefault(value: Int) {
        repeat(length) {
            setDefault(it, value)
        }
    }

    fun setOutOfBounds(value: Int) {
        unsafe.putInt(resetAddress + bytes, value)
    }

    internal fun setDefault(index: Int, value: Int) {
        unsafe.putInt(resetAddress + index * 4L, value)
    }

    fun reset() {
        unsafe.copyMemory(resetAddress, address, bytes)
    }

    fun clear() {
        unsafe.freeMemory(address)
    }

}