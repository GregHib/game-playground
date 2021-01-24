package world.gregs.game.playground.pathfinding.bfs

import world.gregs.game.playground.pathfinding.bfs.unsafe.Memory
import world.gregs.game.playground.pathfinding.bfs.unsafe.UnsafeFifoCircularQueue
import world.gregs.game.playground.pathfinding.bfs.unsafe.Unsafe2DIntArray
import kotlin.math.max
import kotlin.math.min

/**
 * 8-way breadth first search on a 2d plane (with third dimension pass through to [collisions])
 */
class UnsafeBreadthFirstSearch(
    private val collisions: Collisions,
    private val width: Int,
    private val height: Int
) {

    private var visited = 1
    private val queue: UnsafeFifoCircularQueue
    val frontier: Unsafe2DIntArray

    init {
        assert(width <= 0xffff) { "Width cannot exceed 65535" }
        assert(height <= 0xffff) { "Height cannot exceed 65535" }
        val unsafe = Memory.getUnsafe()
        queue = UnsafeFifoCircularQueue(unsafe, width * 2 + height * 2)
        frontier = Unsafe2DIntArray(unsafe, width, height)
        frontier.fill(hash(0, 0, 1))
        frontier.setDefault(0)
    }

    /**
     * Accumulated cost of steps to get to point [x], [y] from the last searches starting point
     * Cardinal movement is twice the cost of diagonal.
     */
    fun getDistance(x: Int, y: Int) = getDistance(frontier[x, y])

    /**
     * Direction from point [x], [y] to the latest searches lowest cost neighbouring point
     */
    fun getDirection(x: Int, y: Int) = getDirection(frontier[x, y])

    /**
     * Check if point [x], [y] was visited in the last search
     */
    fun visited(x: Int, y: Int): Boolean {
        return getVisited(frontier[x, y]) == visited
    }

    /**
     * If is within bounds and hasn't been visited
     */
    private fun unvisited(x: Int, y: Int): Boolean {
        val visited = getVisited(frontier[x, y])
        return visited != 0 && visited != this.visited
    }

    /**
     * Searches starting from point [startX] [startY] expanding the frontier until no nodes are remaining,
     * [target], or [limit] is reached.
     * @param z optional third dimension coordinate passed to [collisions]
     * @param limit the maximum path length allowed
     * @return the hash of the reached node if reached [target] otherwise -1
     */
    fun search(target: TargetPredicate, startX: Int, startY: Int, z: Int = -1, limit: Int = -1): Int {
        reset()
        visit(startX, startY, 1, 0)
        while (queue.isNotEmpty()) {
            val parent = queue.poll()
            val parentX = getX(parent)
            val parentY = getY(parent)
            if (target.reached(parentX, parentY, z)) {
                return parent
            }

            val distance = getDistance(parentX, parentY) + 1
            if (limit != -1 && distance > limit + 1) {
                return -1
            }
            check(parentX, parentY, z, parentX - 1, parentY, distance, WEST)
            check(parentX, parentY, z, parentX + 1, parentY, distance, EAST)
            check(parentX, parentY, z, parentX, parentY - 1, distance, SOUTH)
            check(parentX, parentY, z, parentX, parentY + 1, distance, NORTH)
            check(parentX, parentY, z, parentX - 1, parentY - 1, distance, SOUTH_WEST)
            check(parentX, parentY, z, parentX + 1, parentY - 1, distance, SOUTH_EAST)
            check(parentX, parentY, z, parentX - 1, parentY + 1, distance, NORTH_WEST)
            check(parentX, parentY, z, parentX + 1, parentY + 1, distance, NORTH_EAST)
        }
        return -1
    }
    // TODO benchmark
    fun searchInside(target: TargetPredicate, startX: Int, startY: Int, z: Int = -1, limit: Int = -1): Int {
        reset()
        visit(startX, startY, 1, 0)
        while (queue.isNotEmpty()) {
            val parent = queue.poll()
            val parentX = getX(parent)
            val parentY = getY(parent)

            val distance = getDistance(parentX, parentY) + 1
            if (limit != -1 && distance > limit + 1) {
                return -1
            }
            if(check(target, parentX, parentY, z, parentX - 1, parentY, distance, WEST)) {
                break
            }
            if(check(target, parentX, parentY, z, parentX + 1, parentY, distance, EAST)) {
                break
            }
            if(check(target, parentX, parentY, z, parentX, parentY - 1, distance, SOUTH)) {
                break
            }
            if(check(target, parentX, parentY, z, parentX, parentY + 1, distance, NORTH)) {
                break
            }
            if(check(target, parentX, parentY, z, parentX - 1, parentY - 1, distance, SOUTH_WEST)) {
                break
            }
            if(check(target, parentX, parentY, z, parentX + 1, parentY - 1, distance, SOUTH_EAST)) {
                break
            }
            if(check(target, parentX, parentY, z, parentX - 1, parentY + 1, distance, NORTH_WEST)) {
                break
            }
            if(check(target, parentX, parentY, z, parentX + 1, parentY + 1, distance, NORTH_EAST)) {
                break
            }
        }
        return -1
    }

    private fun reset() {
        visited = max(2, (visited + 1) % 0x7fff)
        queue.reset()
    }

    private fun check(parentX: Int, parentY: Int, z: Int, offsetX: Int, offsetY: Int, distance: Int, direction: Int) {
        if (unvisited(offsetX, offsetY) && !collisions.blocked(parentX, parentY, offsetX, offsetY, direction, z)) {
            visit(offsetX, offsetY, distance, direction)
        }
    }

    private fun check(target: TargetPredicate, parentX: Int, parentY: Int, z: Int, offsetX: Int, offsetY: Int, distance: Int, direction: Int): Boolean {
        if (unvisited(offsetX, offsetY) && !collisions.blocked(parentX, parentY, offsetX, offsetY, direction, z)) {
            if (target.reached(parentX, parentY, z)) {
                return true
            }
            visit(offsetX, offsetY, distance, direction)
        }
        return false
    }

    private fun visit(x: Int, y: Int, distance: Int, direction: Int) {
        frontier[x, y] = hash(distance, direction, visited)
        queue.add(hash(x, y))
    }

    /**
     * Searches a square [radius] for a visited point with the
     * shortest path or closest [TargetPredicate.distance] to the [target]
     */
    fun searchPartial(target: TargetPredicate, z: Int = -1, radius: Int = 10): Int {
        val minX = max(0, target.x - radius)
        val maxX = min(width, target.y + radius)
        val minY = max(0, target.x - radius)
        val maxY = min(height, target.y + radius)
        var lowestCost = Integer.MAX_VALUE
        var lowestDistance = Integer.MAX_VALUE
        var endX = 0
        var endY = 0
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                if (unvisited(x, y)) {
                    continue
                }
                val cost = target.distance(x, y, z)
                if (cost < lowestCost || (cost == lowestCost && getDistance(x, y) < lowestDistance)) {
                    lowestCost = cost
                    lowestDistance = getDistance(x, y)
                    endX = x
                    endY = y
                }
            }
        }

        if (lowestCost == Integer.MAX_VALUE || lowestDistance == Integer.MAX_VALUE) {
            return -1
        }
        return hash(endX, endY)
    }

    /**
     * Invokes [block] with the direction of each valid movement on the path back from [result] to the start
     * Note: calls to [block] will be in the reverse order of the discovered path and opposite direction to [getDirection]
     */
    fun backtrace(result: Int, block: (Int) -> Unit) {
        var traceX = getX(result)
        var traceY = getY(result)
        var distance = getDistance(traceX, traceY)
        var direction = opposite(getDirection(traceX, traceY))
        while (distance > 1) {
            block.invoke(direction)
            when (direction) {
                WEST -> traceX--
                EAST -> traceX++
                SOUTH -> traceY--
                NORTH -> traceY++
                SOUTH_WEST -> {
                    traceX--
                    traceY--
                }
                SOUTH_EAST -> {
                    traceX++
                    traceY--
                }
                NORTH_WEST -> {
                    traceX--
                    traceY++
                }
                NORTH_EAST -> {
                    traceX++
                    traceY++
                }
            }
            distance = getDistance(traceX, traceY)
            direction = opposite(getDirection(traceX, traceY))
        }
    }

    /**
     * Helper combining [search] and [backtrace]
     */
    fun find(target: TargetPredicate, startX: Int, startY: Int, z: Int = -1, limit: Int = -1, block: (Int) -> Unit) {
        val result = search(target, startX, startY, z, limit)
        if (result != -1) {
            backtrace(result, block)
        }
    }

    /**
     * Helper combining [search], [searchPartial] and [backtrace]
     */
    fun findPartial(target: TargetPredicate, startX: Int, startY: Int, z: Int = -1, limit: Int = -1, radius: Int = 10, block: (Int) -> Unit) {
        var result = search(target, startX, startY, z, limit)
        if (result == -1) {
            result = searchPartial(target, radius, z)
        }
        if (result != -1) {
            backtrace(result, block)
        }
    }

    /**
     * Frees allocated memory
     */
    fun free() {
        queue.free()
        frontier.free()
    }

    fun printDist() {
        for (y in height - 1 downTo 0) {
            for (x in 0 until width) {
                val distance = if (visited(x, y)) getDistance(x, y) else 0
                print("$distance${if (distance in 0..9) " " else ""} ")
            }
            println()
        }
    }
    companion object {

        fun hash(x: Int, y: Int) = y or (x shl 16)

        fun getX(hash: Int) = hash shr 16

        fun getY(hash: Int) = hash and 0xffff

        private fun hash(distance: Int, direction: Int, visited: Int) = visited or (direction shl 14) or (distance shl 17)

        private fun getDistance(hash: Int) = hash shr 17

        private fun getDirection(hash: Int) = hash shr 14 and 0x7

        private fun getVisited(hash: Int) = hash and 0x3fff

        const val WEST = 0
        const val EAST = 1
        const val SOUTH = 2
        const val NORTH = 3
        const val SOUTH_WEST = 4
        const val SOUTH_EAST = 5
        const val NORTH_WEST = 6
        const val NORTH_EAST = 7

        private fun opposite(direction: Int): Int {
            return when (direction) {
                WEST -> EAST
                EAST -> WEST
                SOUTH -> NORTH
                NORTH -> SOUTH
                SOUTH_WEST -> NORTH_EAST
                SOUTH_EAST -> NORTH_WEST
                NORTH_WEST -> SOUTH_WEST
                NORTH_EAST -> SOUTH_WEST
                else -> -1
            }
        }
    }
}