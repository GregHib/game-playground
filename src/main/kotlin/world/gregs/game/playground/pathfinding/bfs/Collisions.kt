package world.gregs.game.playground.pathfinding.bfs

interface Collisions {
    /**
     * Check if blocked
     * @param direction 0..7 - w, e, s, n, sw, se, nw, ne
     */
    fun blocked(fromX: Int, fromY: Int, toX: Int, toY: Int, direction: Int, z: Int = -1): Boolean
}