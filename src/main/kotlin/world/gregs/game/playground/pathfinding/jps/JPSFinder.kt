package world.gregs.game.playground.pathfinding.jps

import world.gregs.game.playground.Grid
import world.gregs.game.playground.Node
import world.gregs.game.playground.pathfinding.astar.impl2.AStar2.Companion.getNeighbours
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import world.gregs.game.playground.pathfinding.jps.node.NodeState
import world.gregs.game.playground.pathfinding.jps.node.NodeStatus
import world.gregs.game.playground.ui.zoom.SolidGrid
import java.util.*

class JPSFinder {

    private val jumpList = PriorityQueue<JPSNode2>()

    private var weight: Double = 0.toDouble()
    private var heuristic: ((Node) -> Double)? = null
    private var startTime: Long = 0

    protected fun startClock() {
        startTime = System.nanoTime()
    }

    protected fun stopClock(): Long {
        return System.nanoTime() - startTime
    }

    @Throws(InterruptedException::class)
    fun findPath(map: Grid<JPSNode2>, start: Node, goal: Node, heuristic: (Node) -> Double, weight: Double): Result {

//        if (map !is JPSGrid)
//            throw IllegalArgumentException("JPSFinder can only run on Grid")

        this.weight = weight
        this.heuristic = heuristic

        val startNode = map.get(start.x, start.y)
        val goalNode = map.get(goal.x, goal.y)

        if (startNode == null || goalNode == null)
            throw IllegalArgumentException("Start or Goal node not found in SearchSpace")

        jumpList.clear()
        //closedList.clear();

        startClock()

        // Initialize search by setting start node G cost and adding it to jump list
        startNode.g = 0.0
        jumpList.add(startNode)
        var operations = 0L

        while (!jumpList.isEmpty()) {

            operations++

            val currentNode = jumpList.remove()

            if (currentNode === goalNode) {
                return Result(reconstructPath(currentNode), currentNode.g, stopClock(), operations)
            }

            currentNode.status = NodeStatus.CLOSED
//            addToHistory(currentNode)
            //closedList.add(currentNode);

            identifySuccessors(map, currentNode, startNode, goalNode)

        }
        return Result(ArrayList(), stopClock(), operations)
    }

    protected fun reconstructPath(goal: JPSNode2): List<Node> {
        val reconstructedPath = ArrayList<Node>()

        var current: JPSNode2? = goal
        while (current != null) {
            reconstructedPath.add(current)
            current = current.parent
        }

        return reconstructedPath
    }

    @Throws(InterruptedException::class)
    private fun identifySuccessors(map: Grid<JPSNode2>, currentNode: JPSNode2, startNode: JPSNode2?, goalNode: JPSNode2?) {

        // Start node has no parent, so we can't prune anything
        val neighbours =
            if (currentNode == startNode) getNeighbours(map, currentNode) else pruneNeighbours(map, currentNode)

        for (neighbourNode in neighbours) {

            if (neighbourNode == null)
                continue

            val jumpNode = jump(map, currentNode, getDirection(currentNode, neighbourNode), goalNode!!)

            if (jumpNode == null || jumpNode.status == NodeStatus.CLOSED)
                continue

            // include heuristic distance, as parent might not be immediately adjacent
            val d = octile.invoke(currentNode.delta(jumpNode))
            val g = currentNode.g + d

            if (!jumpList.contains(jumpNode) || g < jumpNode.g) {
                jumpNode.g = g
                jumpNode.h = weight * heuristic!!.invoke(jumpNode.delta(goalNode))

                jumpNode.parent = currentNode

                if (!jumpList.contains(jumpNode)) {
                    jumpList.add(jumpNode)
                    jumpNode.status = NodeStatus.JUMPED
                } else {
                    // Since scores has been recalculated, we remove node from openlist and add it again
                    jumpList.remove(jumpNode)
                    jumpList.add(jumpNode)
                }
            }

//            addToHistory(jumpNode)

        }

    }

    /**
     * When finding neighbours in JPS we only want the node directly in front of the current node, going in the
     * direction of movement, plus any forced neighbours
     *
     * @param node Jump Vertex Successor node to find neighbours of
     * @return A pruned set of neighbours
     */
    private fun pruneNeighbours(map: Grid<JPSNode2>, node: JPSNode2): List<JPSNode2?> {

        val neighbours = ArrayList<JPSNode2?>()

        val parentNode = node.parent

        val direction = getDirection(parentNode!!, node)

        map as SolidGrid

        val dx = direction.x
        val dy = direction.y
        val x = node.x
        val y = node.y
        //map.getMovementCost(parentNode, Node) > 1
        if (dx != 0 && dy != 0) {
            // diagonal movement
            if (!map.blocked(x + dx, y))
                neighbours.add(map!!.get(x + dx, y))

            if (!map.blocked(x, y + dy))
                neighbours.add(map!!.get(x, y + dy))

            if (!map.blocked(x + dx, y + dy))
                neighbours.add(map!!.get(x + dx, y + dy))

            if (MOVING_THROUGH_WALL_CORNERS) {
                if (map.blocked(x - dx, y))
                    neighbours.add(map!!.get(x - dx, y + dy))

                if (map.blocked(x, y - dy))
                    neighbours.add(map!!.get(x + dx, y - dy))
            }

        } else {
            // vertical
            if (dx == 0) {
                // Add node right in front of direction
                if (!map.blocked(x, y + dy)) {
                    neighbours.add(map!!.get(x, y + dy))
                }
                // Pruning changes if corner crossing is allowed
                if (!MOVING_THROUGH_WALL_CORNERS) {
                    if (map.blocked(x - 1, y - dy)) {
                        neighbours.add(map!!.get(x - 1, y))
                        neighbours.add(map!!.get(x - 1, y + dy))
                    }
                    if (map.blocked(x + 1, y - dy)) {
                        neighbours.add(map!!.get(x + 1, y))
                        neighbours.add(map!!.get(x + 1, y + dy))
                    }
                } else {
                    if (map.blocked(x - 1, y)) {
                        neighbours.add(map!!.get(x - 1, y + dy))
                    }
                    if (map.blocked(x + 1, y)) {
                        neighbours.add(map!!.get(x + 1, y + dy))
                    }
                }
                // horizontal
            } else {
                // Add node right in front of direction
                if (!map.blocked(x + dx, y)) {
                    neighbours.add(map!!.get(x + dx, y))
                }
                // Pruning changes if corner crossing is allowed or not
                if (!MOVING_THROUGH_WALL_CORNERS) {
                    if (map.blocked(x - dx, y - 1)) {
                        neighbours.add(map!!.get(x, y - 1))
                        neighbours.add(map!!.get(x + dx, y - 1))
                    }
                    if (map.blocked(x - dx, y + 1)) {
                        neighbours.add(map!!.get(x, y + 1))
                        neighbours.add(map!!.get(x + dx, y + 1))
                    }
                } else {
                    if (map.blocked(x, y - 1)) {
                        neighbours.add(map!!.get(x + dx, y - 1))
                    }
                    if (map.blocked(x, y + 1)) {
                        neighbours.add(map!!.get(x + dx, y + 1))
                    }
                }
            }
        }

        return neighbours
    }

    /**
     * Method jumps from initialNode in direction, until it hits the goal Node, an unwalkable Node, or
     * detects a forced neighbour.
     *
     * The presence of a forced neighbour (depending on the direction of travel, a horizontal or vertical neighbour
     * that is not walkable) means we've found a jump successor.
     *
     * @param initialNode Initial Node to jump from
     * @param direction Direction to jump in
     * @param goalNode Goal Node to look for
     * @return Returns the goal Node, a jump Vertex successor when a forced neighbour is detected, or null if neither
     * was found
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class)
    private fun jump(map: Grid<JPSNode2>, initialNode: JPSNode2, direction: Node, goalNode: JPSNode2): JPSNode2? {

        val node = map.get(initialNode.x + direction.x, initialNode.y + direction.y)

        if (node == null || node.state == NodeState.WALL)
            return null

        if (node === goalNode)
            return node

        if (node.status == NodeStatus.CLOSED || node.status == NodeStatus.JUMPED)
            return null

        node.parent = initialNode
        node.status = NodeStatus.PEEKED
//        addToHistory(node)

        map as SolidGrid

        val x = node.x
        val y = node.y
        val dx = direction.x
        val dy = direction.y

        // Horizontal
        if (dy == 0) {
            if (!MOVING_THROUGH_WALL_CORNERS) {
                if (!map.blocked(x, y - 1) && map.blocked(x - dx, y - 1) || !map.blocked(
                        x,
                        y + 1
                    ) && map.blocked(x - dx, y + 1)
                )
                    return node
            } else {
                if (!map.blocked(x + dx, y - 1) && map.blocked(x, y - 1) || !map.blocked(
                        x + dx,
                        y + 1
                    ) && map.blocked(x, y + 1)
                )
                    return node
            }
            // Vertical
        } else if (dx == 0) {
            if (!MOVING_THROUGH_WALL_CORNERS) {
                if (!map.blocked(x - 1, y) && map.blocked(x - 1, y - dy) || !map.blocked(
                        x + 1,
                        y
                    ) && map.blocked(x + 1, y - dy)
                )
                    return node
            } else {
                if (!map.blocked(x - 1, y + dy) && map.blocked(x - 1, y) || !map.blocked(
                        x + 1,
                        y + dy
                    ) && map.blocked(x + 1, y)
                )
                    return node
            }
        } else {
            // If we're moving in a diagonal direction, first check for forced neighbours ...
            if (MOVING_THROUGH_WALL_CORNERS) {
                if (map.blocked(x - dx, y) && !map.blocked(x - dx, y + dy) || map.blocked(
                        x,
                        y - dy
                    ) && !map.blocked(x + dx, y - dy)
                )
                    return node
            } else {
                if (map.blocked(x - dx, y) && map.blocked(x, y - dy))
                    return null
            }

            // ... then jump horizontally and vertically from diagonal position
            if (jump(map, node, Node(dx, 0), goalNode) != null || jump(map, node, Node(0, dy), goalNode) != null)
                return node
        }

        return jump(map, node, direction, goalNode)
    }

    private fun getDirection(from: JPSNode2, to: JPSNode2): Node {

        val dx = Math.max(-1, Math.min(1, to.x - from.x))
        val dy = Math.max(-1, Math.min(1, to.y - from.y))

        return Node(dx, dy)

    }

    companion object {

        const val MOVING_THROUGH_WALL_CORNERS = true
        val octile: (Node) -> Double = { d -> Math.max(d.x, d.y) + (Math.sqrt(2.0) - 1) * Math.min(d.x, d.y) }
    }

}
