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

    companion object {
        val cardinal = values().filter { direction -> direction.x == 0 || direction.y == 0 }.toTypedArray()
    }
}