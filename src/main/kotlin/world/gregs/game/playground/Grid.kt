package world.gregs.game.playground

import java.util.*
import kotlin.random.Random

class Grid(val columns: Int, val rows: Int) {
    private val tiles = Array(columns) { Array(rows) { false } }

    fun blocked(x: Int, y: Int, default: Boolean = true): Boolean = tiles.getOrNull(x)?.getOrNull(y) ?: default

    fun set(x: Int, y: Int, value: Boolean) {
        tiles[x][y] = value
    }

    fun clear() {
        tiles.forEach { Arrays.fill(it, false) }
    }

    fun fillRandom(percent: Double) {
        check(percent <= 1) { "Percentage must be between 0-1"}
        check(percent > 0) { "Percentage must be between 0-1" }
        clear()
        for (x in 0 until columns) {
            for (y in 0 until rows) {
                set(x, y, Random.nextDouble() < percent)
            }
        }
    }
}