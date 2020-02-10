package world.gregs.game.playground.pathfinding.astar.impl2

import world.gregs.game.playground.Grid
import world.gregs.game.playground.Node
import world.gregs.game.playground.pathfinding.astar.BinaryHashHeap
import world.gregs.game.playground.pathfinding.jps.AbstractPathfinder
import world.gregs.game.playground.pathfinding.jps.Result
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import world.gregs.game.playground.pathfinding.jps.node.NodeStatus
import world.gregs.game.playground.ui.zoom.SolidGrid
import kotlin.math.abs
import kotlin.math.sqrt

class AStar2 : AbstractPathfinder() {

    fun findPath(map: Grid<JPSNode2>, start: Node, goal: Node, heuristic: (Node) -> Double, weight: Double): Result {

        val startNode = map.get(start.x, start.y)
        val goalNode = map.get(goal.x, goal.y)

        require(!(startNode == null || goalNode == null)) { "Start or Goal node not found in SearchSpace" }

        // Hash heap seems to net ~10ms on 512x512 maps, but what is the memory impact?
        val openList = BinaryHashHeap(JPSNode2::class.java, map.columns * map.rows)
//        val openList = LinkedList<JPSNode2>()

        startClock()

        // Initialize search by setting G score of start to 0 and adding it to open list
        startNode.g = 0.0
        openList.add(startNode)

        // While we still have nodes to check in the open list
        while (openList.size > 0) {

            operations++

            // Get node with best score from open list
            val currentNode = openList.remove()

            // If we've reached the goal, reconstruct complete path
            if (currentNode == goalNode) {
                println("Found goal!")
                return Result(reconstructPath(currentNode), currentNode.g, stopClock(), operations)
            }

            // Close node to indicate it has been checked
            currentNode.status = NodeStatus.CLOSED

//            addToHistory(currentNode)

            // Go through all node neighbours
            for (neighbourNode in getNeighbours(map, currentNode)) {

                // If neighbour is already closed, skip it
                if (neighbourNode.status == NodeStatus.CLOSED)
                    continue

                // Calculate G cost for neighbour node
                val g = currentNode.g + getMovementCost(currentNode, neighbourNode)

                // Use node's own status instead of openList.contains() which is O(n) when using binary heap
                val isInOpenList = neighbourNode.status == NodeStatus.OPEN

                // If neighbour node is not on the open list, or the current G cost of neighbour
                // node is higher than newly calculated G cost
                if (!isInOpenList || g < neighbourNode.g) {
                    // Set new G and H costs, and parent
                    neighbourNode.g = g
                    neighbourNode.h = weight * heuristic.invoke(neighbourNode.delta(goalNode))
                    neighbourNode.parent = currentNode

                    // Add node to open list if it's not on it already
                    if (!isInOpenList) {
                        neighbourNode.status = NodeStatus.OPEN
                        openList.add(neighbourNode)
                    } else {
                        // If node is already on the open list, update it so list gets resorted
                        openList.update(neighbourNode)
                    }

//                    addToHistory(neighbourNode)

                }

            }

        }

        println("Could not find goal!")
        return Result(ArrayList(), java.lang.Double.MAX_VALUE, stopClock(), operations)
    }

    fun getMovementCost(n1: JPSNode2, n2: JPSNode2): Double {
        val dx = abs(n1.x - n2.x).toDouble()
        val dy = abs(n1.y - n2.y).toDouble()

        return if (dx == 0.0 || dy == 0.0) 1.0 else sqrt(2.0)
    }

    companion object {
        fun getNeighbours(grid: Grid<JPSNode2>, node: JPSNode2): List<JPSNode2> {
            val neighbours = ArrayList<JPSNode2>()
            val x = node.x
            val y = node.y

            val directions = ArrayList<Node>()

            var u = false
            var r = false
            var d = false
            var l = false

            grid as SolidGrid

            if (!grid.blocked(x, y - 1)) {
                directions.add(Node(x, y - 1)) // up
                u = true
            }
            if (!grid.blocked(x + 1, y)) {
                directions.add(Node(x + 1, y)) // right
                r = true
            }
            if (!grid.blocked(x, y + 1)) {
                directions.add(Node(x, y + 1)) // down
                d = true
            }
            if (!grid.blocked(x - 1, y)) {
                directions.add(Node(x - 1, y)) // left
                l = true
            }

            if (DIAGONAL_MOVEMENT) {

                if (!MOVE_THROUGH_WALL_CORNERS) {

                    val ul = u && l
                    val ur = u && r
                    val dr = d && r
                    val dl = d && l

                    if (ul && !grid.blocked(x - 1, y - 1))
                        directions.add(Node(x - 1, y - 1)) // upleft
                    if (ur && !grid.blocked(x + 1, y - 1))
                        directions.add(Node(x + 1, y - 1)) // upright
                    if (dr && !grid.blocked(x + 1, y + 1))
                        directions.add(Node(x + 1, y + 1)) // downright
                    if (dl && !grid.blocked(x - 1, y + 1))
                        directions.add(Node(x - 1, y + 1)) // downleft
                } else {
                    if (!grid.blocked(x - 1, y - 1))
                        directions.add(Node(x - 1, y - 1)) // upleft
                    if (!grid.blocked(x + 1, y - 1))
                        directions.add(Node(x + 1, y - 1)) // upright
                    if (!grid.blocked(x + 1, y + 1))
                        directions.add(Node(x + 1, y + 1)) // downright
                    if (!grid.blocked(x - 1, y + 1))
                        directions.add(Node(x - 1, y + 1)) // downleft
                }
            }

            for (p in directions) {
                neighbours.add(grid.get(p.x, p.y) ?: continue)
            }

            return neighbours
        }

        const val DIAGONAL_MOVEMENT = true
        const val MOVE_THROUGH_WALL_CORNERS = true
        val manhattan: (Node) -> Double = { it.x + it.y.toDouble() }
    }


}

