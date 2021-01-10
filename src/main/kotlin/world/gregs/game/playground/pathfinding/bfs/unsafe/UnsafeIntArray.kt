package world.gregs.game.playground.pathfinding.bfs.unsafe

import sun.misc.Unsafe

class UnsafeIntArray(
    private val unsafe: Unsafe,
    private val length: Int
) {
    private val address = unsafe.allocateMemory(index(length))
    private var default: Int = -1

    private fun index(value: Int): Long = value * 4L

    /**
     * Set value at [index]
     * @throws [IndexOutOfBoundsException] if out of bounds
     */
    operator fun set(index: Int, value: Int) {
        if (index < 0 || index >= length) {
            throw IndexOutOfBoundsException("Index $index is not within bounds: $length.")
        }
        unsafe.putInt(address + index(index), value)
    }

    /**
     * Get value stored at [index] or [default] if out of bounds
     */
    operator fun get(index: Int): Int {
        if (index < 0 || index >= length) {
            return default
        }
        return unsafe.getInt(address + index(index))
    }

    /**
     * Fills the array with [value]
     */
    fun fill(value: Int) {
        repeat(length) {
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