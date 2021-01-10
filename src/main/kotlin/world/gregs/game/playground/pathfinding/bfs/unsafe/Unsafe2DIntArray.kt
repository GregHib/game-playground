package world.gregs.game.playground.pathfinding.bfs.unsafe

import sun.misc.Unsafe

class Unsafe2DIntArray(
    private val unsafe: Unsafe,
    private val width: Int,
    private val height: Int
) {
    private val address = unsafe.allocateMemory(index(width * height))
    private var default: Int = -1

    private fun index(value: Int) = value * 4L
    private fun hash(x: Int, y: Int): Long = index(x + (y * width))

    /**
     * Set value at [x], [y]
     * @throws [IndexOutOfBoundsException] if out of bounds
     */
    operator fun set(x: Int, y: Int, value: Int) {
        if (x < 0 || x >= width) {
            throw IndexOutOfBoundsException("X coordinate $x is not within bounds: $width.")
        }
        if (y < 0 || y >= height) {
            throw IndexOutOfBoundsException("Y coordinate $y is not within bounds: $height.")
        }
        unsafe.putInt(address + hash(x, y), value)
    }

    /**
     * Get value stored at [x], [y] or [default] if out of bounds
     */
    operator fun get(x: Int, y: Int): Int {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return default
        }
        return unsafe.getInt(address + hash(x, y))
    }

    /**
     * Fills the array with [value]
     */
    fun fill(value: Int) {
        repeat(width * height) {
            unsafe.putInt(address + index(it), value)
        }
    }

    /**
     * Set the default value to return when out of bounds
     */
    fun setDefault(value: Int) {
        default = value
    }

    /**
     * Frees allocated memory
     */
    fun free() {
        unsafe.freeMemory(address)
    }
}