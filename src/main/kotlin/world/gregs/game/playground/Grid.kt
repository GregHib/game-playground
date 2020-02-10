package world.gregs.game.playground

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import java.security.SecureRandom

open class Grid<T>(val columns: Int, val rows: Int) {

    private val grid = Long2ObjectOpenHashMap<T>(columns * rows)
    private val keys: LongArray = LongArray(columns.coerceAtLeast(rows) * columns.coerceAtLeast(rows))
    private val rand = SecureRandom()

    val colIndices = 0 until columns
    val rowIndices = 0 until rows

    fun set(x: Int, y: Int, value: T) {
        val hash = hash(x, y)
        keys[y * rows + x] = hash
        grid[hash] = value
    }

    fun get(x: Int, y: Int): T? {
        return if (x in colIndices && y in rowIndices) grid[keys[y * rows + x]] else null
    }

    private fun hash(x: Int, y: Int): Long {
        var hash = rand.nextLong()

        hash = hash xor x.toLong()
        hash = hash xor y.toLong()

        return hash
    }

    fun clear() {
        grid.clear()
        keys.fill(0)
    }
}