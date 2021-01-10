package world.gregs.game.playground.pathfinding.bfs

interface TargetPredicate {
    val x: Int

    val y: Int

    /**
     * Check if point [x], [y] is close enough to interact with the target
     */
    fun reached(x: Int, y: Int, z: Int = -1): Boolean

    /**
     * Calculates the distance between [x], [y] and target, used to
     * determine the closest point to the target in partial searches.
     * Return -1 if not needed.
     */
    fun distance(x: Int, y: Int, z: Int = -1): Int
}