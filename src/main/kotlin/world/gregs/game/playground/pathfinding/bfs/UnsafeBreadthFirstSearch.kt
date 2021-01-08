package world.gregs.game.playground.pathfinding.bfs

import sun.misc.Unsafe
import world.gregs.game.playground.pathfinding.bfs.unsafe.Unsafe2DIntArray
import world.gregs.game.playground.pathfinding.bfs.unsafe.UnsafeIntQueue
import java.lang.reflect.Field

/**
 * 8-way breadth first search on a 2d plane (with third dimension pass through to [collisions])
 */
class UnsafeBreadthFirstSearch(
    private val collisions: Collisions,
    unsafe: Unsafe,
    width: Int,
    height: Int
) {

    private val queue = UnsafeIntQueue(unsafe, width * height)
    private val frontier = Unsafe2DIntArray(unsafe, width, height)

    init {
        assert(width <= 0xffff) { "Width cannot exceed 65535" }
        assert(height <= 0xffff) { "Height cannot exceed 65535" }
        queue.setDefault(-1)
        frontier.setDefault(0)
        frontier.setOutOfBounds(0)
    }

    /**
     * Accumulated cost of steps to get to point [x], [y] from the last searches starting point
     * Cardinal movement is twice the cost of diagonal.
     */
    fun getCost(x: Int, y: Int) = getCost(frontier[x, y])

    /**
     * Direction from point [x], [y] to the latest searches lowest cost neighbouring point
     */
    fun getDirection(x: Int, y: Int) = getDirection(frontier[x, y])

    /**
     * Distance from [x], [y] to the last searches target according to [TargetPredicate]
     */
    fun getDistance(x: Int, y: Int) = getDistance(frontier[x, y])

    /**
     * Check if point [x], [y] was visited in the last search
     */
    fun visited(x: Int, y: Int): Boolean {
        if (frontier.outOfBounds(x, y)) {
            return false
        }
        return getDistance(frontier[x, y]) > 0
    }

    fun unvisited(x: Int, y: Int): Boolean {
        if (frontier.outOfBounds(x, y)) {
            return false
        }
        return getDistance(frontier[x, y]) == 0
    }

    /**
     * Searches starting from point [startX] [startY] expanding the frontier until no nodes are remaining or [target] is reached.
     * @param z optional third dimension coordinate passed to [collisions]
     * @return the hash of the reached node if reached [target] otherwise -1
     */
    fun search(target: TargetPredicate, startX: Int, startY: Int, z: Int = -1): Int {
        reset()
        visit(startX, startY, 1, 0, MAX_DISTANCE)

        while (queue.isNotEmpty()) {
            val parent = queue.poll()
            val parentX = getX(parent)
            val parentY = getY(parent)
            if (target.reached(parentX, parentY, z)) {
                return parent
            }
            check(parentX, parentY, z, parentX - 1, parentY, WEST)
            check(parentX, parentY, z, parentX + 1, parentY, EAST)
            check(parentX, parentY, z, parentX, parentY - 1, SOUTH)
            check(parentX, parentY, z, parentX, parentY + 1, NORTH)
            check(parentX, parentY, z, parentX - 1, parentY - 1, SOUTH_WEST)
            check(parentX, parentY, z, parentX + 1, parentY - 1, SOUTH_EAST)
            check(parentX, parentY, z, parentX - 1, parentY + 1, NORTH_WEST)
            check(parentX, parentY, z, parentX + 1, parentY + 1, NORTH_EAST)
        }
        return -1
    }

    private fun check(parentX: Int, parentY: Int, z: Int, offsetX: Int, offsetY: Int, direction: Int) {
        if (unvisited(offsetX, offsetY) && !collisions.blocked(parentX, parentY, offsetX, offsetY, direction, z)) {
            val cost = getCost(parentX, parentY) + if (direction < 4) 2 else 1
            visit(offsetX, offsetY, cost, direction)
        }
    }

    /**
     * Searches starting from point [startX] [startY] expanding the frontier until [target] is reached or the closest tile is found
     * @param z optional third dimension coordinate passed to [collisions]
     * @return the hash of the reached node if reached [target] otherwise -1
     */
    fun searchPartial(target: TargetPredicate, startX: Int, startY: Int, z: Int = -1): Int {
        reset()
        visit(startX, startY, 1, 0, MAX_DISTANCE)
        var lowestCost = Int.MAX_VALUE
        var lowestDistance = Int.MAX_VALUE
        var lowestPoint = -1

        fun check(parentX: Int, parentY: Int, offsetX: Int, offsetY: Int, direction: Int) {
            if (unvisited(offsetX, offsetY) && !collisions.blocked(parentX, parentY, offsetX, offsetY, direction, z)) {
                var distance = target.distance(offsetX, offsetY, z)
                if (distance < 0) {
                    distance = MAX_DISTANCE
                }
                val cost = getCost(parentX, parentY) + if (direction < 4) 2 else 1
                visit(offsetX, offsetY, cost, direction, distance)
                if (distance < lowestCost || distance == lowestCost && cost < lowestDistance) {
                    lowestCost = distance
                    lowestDistance = cost
                    lowestPoint = hash(offsetX, offsetY)
                }
            }
        }

        while (queue.isNotEmpty()) {
            val parent = queue.poll()
            val parentX = getX(parent)
            val parentY = getY(parent)
            if (target.reached(parentX, parentY, z)) {
                return parent
            }
            check(parentX, parentY, parentX - 1, parentY, WEST)
            check(parentX, parentY, parentX + 1, parentY, EAST)
            check(parentX, parentY, parentX, parentY - 1, SOUTH)
            check(parentX, parentY, parentX, parentY + 1, NORTH)
            check(parentX, parentY, parentX - 1, parentY - 1, SOUTH_WEST)
            check(parentX, parentY, parentX + 1, parentY - 1, SOUTH_EAST)
            check(parentX, parentY, parentX - 1, parentY + 1, NORTH_WEST)
            check(parentX, parentY, parentX + 1, parentY + 1, NORTH_EAST)
        }
        if (lowestCost == Int.MAX_VALUE || lowestDistance == Int.MAX_VALUE) {
            return -1
        }
        return lowestPoint
    }

    private fun visit(x: Int, y: Int, cost: Int, direction: Int, distance: Int = MAX_DISTANCE) {
        frontier[x, y] = hash(cost, direction, distance)
        queue.add(hash(x, y))
    }

    /**
     * Invokes [block] with the direction of each valid movement on the path back from [x], [y] to the start
     * Note: calls to [block] will be in the reverse order of the discovered path and opposite direction to [getDirection]
     */
    fun backtrace(x: Int, y: Int, block: (Int) -> Unit) {
        var traceX = x
        var traceY = y
        var distance = getCost(traceX, traceY)
        var direction = getDirection(traceX, traceY)
        while (distance > 0) {
            block.invoke(opposite(direction))
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
            distance = getCost(traceX, traceY)
            direction = getDirection(traceX, traceY)
        }
    }

    private fun reset() {
        queue.reset()
        frontier.reset()
    }

    /**
     * Frees allocated memory
     */
    fun clear() {
        queue.clear()
        frontier.clear()
    }

    companion object {

        fun getUnsafe(): Unsafe {
            val f: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
            f.isAccessible = true
            return f.get(null) as Unsafe
        }

        fun hash(x: Int, y: Int) = y or (x shl 16)

        private fun hash(cost: Int, direction: Int, distance: Int) = cost or (direction shl 14) or (distance shl 17)

        fun getX(hash: Int) = hash shr 16

        fun getY(hash: Int) = hash and 0xffff

        private fun getCost(hash: Int) = hash and 0x3fff

        private fun getDirection(hash: Int) = hash shr 14 and 0x7

        private fun getDistance(hash: Int) = hash shr 17 and 0x7fff

        @JvmStatic
        fun main(args: Array<String>) {
            println(getDistance(0))
            for (dist in 0..0x3fff) {
                for (d in 0..7) {
                    for (cost in 0..0x7fff) {
                        if (hash(dist, d, cost) == 0) {
                            println("Uh oh $dist $d $cost")
                        }
                    }
                }
            }
        }

        const val MAX_DISTANCE = 0x7fff

        const val WEST = 0
        const val EAST = 1
        const val SOUTH = 2
        const val NORTH = 3
        const val SOUTH_WEST = 4
        const val SOUTH_EAST = 5
        const val NORTH_WEST = 6
        const val NORTH_EAST = 7

        fun opposite(direction: Int): Int {
            return when (direction) {
                WEST -> EAST
                EAST -> WEST
                SOUTH -> NORTH
                NORTH -> SOUTH
                SOUTH_WEST -> NORTH_EAST
                SOUTH_EAST -> NORTH_WEST
                NORTH_WEST -> SOUTH_WEST
                NORTH_EAST -> SOUTH_EAST
                else -> -1
            }
        }
    }
}