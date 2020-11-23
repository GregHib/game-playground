package world.gregs.game.playground.spatial.sight

import world.gregs.game.playground.ui.zoom.SolidGrid
import kotlin.math.abs

class LineOfSight(private val grid: SolidGrid) {

    private fun blocked(x: Int, y: Int, flip: Boolean): Boolean {
        return if (flip) {
            grid.blocked(y, x)
        } else {
            grid.blocked(x, y)
        }
    }

    /**
     * Bidirectional impl of [canSee]
     */
    fun canSeeBi(x: Int, y: Int, targetX: Int, targetY: Int): Boolean {
        return canSee(x, y, targetX, targetY) && canSee(targetX, targetY, x, y)
    }

    /**
     * A variation of Bresenham's line algorithm which marches from starting point [x], [y]
     * alternating axis until reaching a blockage or target [targetX], [targetY]
     * @return whether there is nothing blocking between the two points
     */
    fun canSee(x: Int, y: Int, targetX: Int, targetY: Int): Boolean {
        if (x == targetX && y == targetY) {
            return true
        }

        var dx = targetX - x
        var dy = targetY - y
        var dxAbs = abs(dx)
        val dyAbs = abs(dy)

        val flip = dxAbs <= dyAbs

        if (flip) {
            val temp = dx
            dx = dy
            dy = temp
            dxAbs = dyAbs
        }

        var shifted: Int = shift(if (flip) x else y)
        shifted += shiftedHalfTile
        if (needsRounding(dy)) {
            shifted--
        }

        var position: Int = if (flip) y else x
        val target = if (flip) targetY else targetX

        val direction = if (dx < 0) -1 else 1
        val slope = shift(dy) / dxAbs
        while (position != target) {

            position += direction
            val value = revert(shifted)
            if (blocked(position, value, flip)) {
                return false
            }

            shifted += slope
            val next = revert(shifted)
            if (next != value && blocked(position, next, flip)) {
                return false
            }
        }

        return true
    }

    /**
     * Shift values to avoid rounding errors
     */
    private fun shift(value: Int) = value shl 16

    private fun revert(value: Int) = value ushr 16

    private fun needsRounding(value: Int) = value < 0

    companion object {
        private const val shiftedHalfTile = 0x8000
    }

}