package world.gregs.game.playground

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, 1),
    NORTH_EAST(1, 1),
    EAST(1, 0),
    SOUTH_EAST(1, -1),
    SOUTH(0, -1),
    SOUTH_WEST(-1, -1),
    WEST(-1, 0),
    NORTH_WEST(-1, 1);

    fun isDiagonal() = x != 0 && y != 0

    fun inverse(): Direction {
        return when(this) {
            NORTH_WEST -> SOUTH_EAST
            NORTH -> SOUTH
            NORTH_EAST -> SOUTH_WEST
            EAST -> WEST
            SOUTH_EAST -> NORTH_WEST
            SOUTH -> NORTH
            SOUTH_WEST -> NORTH_EAST
            WEST -> EAST
        }
    }

    companion object {
        val cardinal = values().filter { direction -> direction.x == 0 || direction.y == 0 }.toTypedArray()
        val all = arrayOf(NORTH, EAST, SOUTH, WEST, NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST)
    }
}