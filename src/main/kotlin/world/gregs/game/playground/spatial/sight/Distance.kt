package world.gregs.game.playground.spatial.sight

object Distance {
    fun getNearest(source: Int, size: Int, target: Int): Int {
        val max = source + size - 1
        return when {
            target > max -> max
            target < source -> source
            else -> target
        }
    }
}