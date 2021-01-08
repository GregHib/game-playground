package world.gregs.game.playground.pathfinding.bfs.unsafe

import sun.misc.Unsafe

class Unsafe2DIntArray(
    private val unsafe: Unsafe,
    private val width: Int,
    private val height: Int
) {
    private val bytes = (width * height) * 4L
    private val address = unsafe.allocateMemory(bytes)
    private val resetAddress = unsafe.allocateMemory(bytes + 4L)

    private fun offset(x: Int, y: Int): Long = (x + (y * width)) * 4L

    operator fun set(x: Int, y: Int, value: Int) {
        if (x >= width || x < 0) {
            throw IndexOutOfBoundsException("X coordinate $x is not within bounds: $width.")
        }
        if (y >= height || y < 0) {
            throw IndexOutOfBoundsException("Y coordinate $y is not within bounds: $height.")
        }
        unsafe.putInt(address + offset(x, y), value)
    }

    operator fun get(x: Int, y: Int): Int {
        if (outOfBounds(x, y)) {
            return unsafe.getInt(resetAddress + bytes)
        }
        return unsafe.getInt(address + offset(x, y))
    }

    fun outOfBounds(x: Int, y: Int) = x < 0 || y < 0 || x >= width || y >= height

    fun setDefault(value: Int) {
        repeat(width * height) {
            setDefault(it, value)
        }
    }

    fun setDefault(index: Int, value: Int) {
        unsafe.putInt(resetAddress + (index * 4), value)
    }

    fun setOutOfBounds(value: Int) {
        unsafe.putInt(resetAddress + bytes, value)
    }

    fun reset() {
        unsafe.copyMemory(resetAddress, address, bytes)
    }

    fun clear() {
        unsafe.freeMemory(address)
    }

}