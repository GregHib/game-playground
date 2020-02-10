package world.gregs.game.playground.pathfinding.astar.impl2

import world.gregs.game.playground.Grid
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import world.gregs.game.playground.pathfinding.jps.node.NodeState
import world.gregs.game.playground.ui.zoom.SolidGrid

object AStarTest2 {
    @JvmStatic
    fun main(args: Array<String>) {

        val grid = object : Grid<JPSNode2>(8, 8), SolidGrid {
            override fun blocked(x: Int, y: Int): Boolean {
                return get(x, y)?.state == NodeState.WALL
            }
        }

        for (x in grid.colIndices) {
            for (y in grid.rowIndices) {
                grid.set(x, y, JPSNode2(x, y))
            }
        }

        grid.get(1, 6)?.state = NodeState.WALL
        grid.get(2, 6)?.state = NodeState.WALL
        grid.get(3, 6)?.state = NodeState.WALL
        grid.get(4, 6)?.state = NodeState.WALL
        val start = grid.get(1, 1)!!
        val end = grid.get(2, 7)!!

        val result = AStar2().findPath(grid, start, end, AStar2.manhattan, 1.0)
        for (v in result.path()) {
            System.out.println(v)
        }
        println(String.format("%d ms %d ops", result.duration(), result.operations()))
    }
}