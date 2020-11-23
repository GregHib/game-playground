package world.gregs.game.playground

import world.gregs.game.playground.ui.zoom.SolidGrid
import kotlin.random.Random

class BooleanGrid(columns: Int, rows: Int) : Grid<Boolean>(columns, rows), SolidGrid {

    fun blocked(x: Int, y: Int, default: Boolean = true): Boolean = get(x, y) ?: default

    fun fillRandom(percent: Double) {
        check(percent <= 1) { "Percentage must be between 0-1" }
        check(percent > 0) { "Percentage must be between 0-1" }
        clear()
        for (x in 0 until columns) {
            for (y in 0 until rows) {
                set(x, y, Random.nextDouble() < percent)
            }
        }
    }

    override fun blocked(x: Int, y: Int): Boolean {
        return get(x, y) == true
    }
}