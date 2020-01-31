package world.gregs.game.playground.pathfinding.astar

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

        var LURDMoves = arrayOf(
            intArrayOf(-1, 0),
            intArrayOf(0, -1),
            intArrayOf(1, 0),
            intArrayOf(0, 1)
        )

        var diagonalMoves = arrayOf(
            intArrayOf(-1, -1),
            intArrayOf(1, -1),
            intArrayOf(1, 1),
            intArrayOf(-1, 1)
        )
        //references to the LURDMoves entries that would block the diagonal
        //if they are both walls and canPassThroughCorners = false
        var DiagonalBlockers = arrayOf(
            intArrayOf(0, 1),
            intArrayOf(1, 2),
            intArrayOf(2, 3),
            intArrayOf(3, 0)
        )
    }

    fun addNeighbors(grid: Array<Array<AStarNode>>) {
        // Left/up/right/down
        for (i in 0 until 4) {
            val node = grid.getOrNull(x + LURDMoves[i][0])?.getOrNull(y + LURDMoves[i][1])
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
            val gridX = x + diagonalMoves[i][0]
            val gridY = y + diagonalMoves[i][1]

            val node = grid.getOrNull(gridX)?.getOrNull(gridY)
            if (node != null) {
                if (allowDiagonals && !node.wall) {
                    if (!canPassThroughCorners) {
                        // Check if blocked by surrounding walls
                        val border1 = DiagonalBlockers[i][0]
                        val border2 = DiagonalBlockers[i][1]

                        // No need to protect against OOB as diagonal move
                        // check ensures that blocker refs must be valid
                        val blocker1 = grid[x + LURDMoves[border1][0]][y + LURDMoves[border1][1]]
                        val blocker2 = grid[x + LURDMoves[border2][0]][y + LURDMoves[border2][1]]
                        if (!blocker1.wall || !blocker2.wall) {
                            // one or both are open so we can move past
                            neighbors.add(node)
                        }
                    } else {
                        neighbors.add(node)
                    }
                }
                if (node.wall) {
                    this.neighboringWalls.push(node);
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