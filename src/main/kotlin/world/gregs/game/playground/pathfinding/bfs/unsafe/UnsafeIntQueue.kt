package world.gregs.game.playground.pathfinding.bfs.unsafe

import sun.misc.Unsafe

/**
 * FIFO queue of ints
 */
class UnsafeIntQueue(
    unsafe: Unsafe,
    length: Int
) {

    private val array = UnsafeIntArray(unsafe, length + 2)

    var writeIndex: Int
        set(value) {
            array[0] = value
        }
        get() = array[0]

    var readIndex: Int
        set(value) {
            array[1] = value
        }
        get() = array[1]

    fun isEmpty() = readIndex >= writeIndex

    fun isNotEmpty() = readIndex < writeIndex

    fun add(value: Int) {
        array[writeIndex++ + 2] = value
    }

    fun peek(): Int {
        return array[readIndex + 2]
    }

    fun poll(): Int {
        return array[readIndex++ + 2]
    }

    operator fun set(index: Int, value: Int) {
        array[index + 2] = value
    }

    operator fun get(index: Int): Int {
        return array[index + 2]
    }

    fun setDefault(value: Int) {
        array.setDefault(value)
        array.setDefault(0, 0)
        array.setDefault(1, 0)
    }

    fun reset() {
        array.reset()
    }

    fun clear() {
        array.clear()
    }

}