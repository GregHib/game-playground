package world.gregs.game.playground.pathfinding.astar.impl

import world.gregs.game.playground.Grid
import world.gregs.game.playground.pathfinding.astar.AStarView.Companion.allowDiagonals
import world.gregs.game.playground.pathfinding.astar.AStarView.Companion.canPassThroughCorners
import java.awt.Point
import java.util.*

class AStarNode(
    x: Int,
    y: Int,
    var f: kotlin.Double = 0.0,
    var g: kotlin.Double = 0.0,
    var vh: kotlin.Double = 0.0,
    var h: kotlin.Double = 0.0
) : Point(x, y) {
    val neighbors = LinkedList<AStarNode>()
    val neighboringWalls = LinkedList<AStarNode>()
    var previous: AStarNode? = null
    var wall = false

    companion object {

        var LURD_MOVES = arrayOf(
            intArrayOf(-1, 0),
            intArrayOf(0, -1),
            intArrayOf(1, 0),
            intArrayOf(0, 1)
        )

        var DIAGONAL_MOVES = arrayOf(
            intArrayOf(-1, -1),
            intArrayOf(1, -1),
            intArrayOf(1, 1),
            intArrayOf(-1, 1)
        )
        //references to the LURDMoves entries that would block the diagonal
        //if they are both walls and canPassThroughCorners = false
        var DIAGONAL_BLOCKERS = arrayOf(
            intArrayOf(0, 1),
            intArrayOf(1, 2),
            intArrayOf(2, 3),
            intArrayOf(3, 0)
        )
    }

    fun addNeighbors(grid: Grid<AStarNode>) {
        // Left/up/right/down
        for (i in 0 until 4) {
            val node = grid.get(x + LURD_MOVES[i][0], y + LURD_MOVES[i][1])
            if (node != null) {
                if(!node.wall) {
                    neighbors.add(node)
                } else {
                    neighboringWalls.add(node)
                }
            }
        }

        // Diagonals
        for (i in 0 until 4) {
            val gridX = x + DIAGONAL_MOVES[i][0]
            val gridY = y + DIAGONAL_MOVES[i][1]

            val node = grid.get(gridX, gridY)
            if (node != null) {
                if (allowDiagonals && !node.wall) {
                    if (!canPassThroughCorners) {
                        // Check if blocked by surrounding walls
                        val border1 = DIAGONAL_BLOCKERS[i][0]
                        val border2 = DIAGONAL_BLOCKERS[i][1]

                        // No need to protect against OOB as diagonal move
                        // check ensures that blocker refs must be valid
                        val blocker1 = grid.get(x + LURD_MOVES[border1][0], y + LURD_MOVES[border1][1])
                        val blocker2 = grid.get(x + LURD_MOVES[border2][0], y + LURD_MOVES[border2][1])
                        if (blocker1?.wall == false || blocker2?.wall == false) {
                            // one or both are open so we can move past
                            neighbors.add(node)
                        }
                    } else {
                        neighbors.add(node)
                    }
                }
                if (node.wall) {
                    this.neighboringWalls.push(node)
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AStarNode) {
            return false
        }

        return x == other.x && y == other.y
    }
}