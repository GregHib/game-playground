package world.gregs.game.playground.pathfinding.jps

import world.gregs.game.playground.Grid
import world.gregs.game.playground.euclidean
import world.gregs.game.playground.manhattan
import world.gregs.game.playground.pathfinding.astar.impl2.AStar2
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import world.gregs.game.playground.pathfinding.jps.node.NodeState
import world.gregs.game.playground.ui.zoom.SolidGrid


object JPSFinderTest {

    @JvmStatic
    fun main(args: Array<String>) {

        val grid = object : Grid<JPSNode2>(6, 6), SolidGrid {
            override fun blocked(x: Int, y: Int): Boolean {
                return get(x, y)?.state == NodeState.WALL
            }
        }
        for (x in grid.colIndices) {
            for (y in grid.rowIndices) {
                grid.set(x, y, JPSNode2(x, y))
            }
        }

        grid.get(1, 1)!!.state = NodeState.WALL
        grid.get(2, 1)?.state = NodeState.WALL
        grid.get(3, 1)?.state = NodeState.WALL
        grid.get(4, 1)?.state = NodeState.WALL
        grid.get(5, 1)?.state = NodeState.WALL
        grid.get(1, 2)?.state = NodeState.WALL
        grid.get(1, 3)?.state = NodeState.WALL
        grid.get(2, 3)?.state = NodeState.WALL
//        start.state = NodeState.EMPTY
//        end.state = NodeState.EMPTY
        for (y in grid.rowIndices.reversed()) {
            for (x in grid.colIndices) {
//                println("$x, $y ${grid.get(x, y)?.state}")
                print("${if(grid.get(x, y)?.state == NodeState.WALL) 1 else 0} ")
            }
            println()
        }
        val start = grid.get(5, 0)!!
        val end = grid.get(5, 2)!!

        val f = JPSFinder()

        val r: Result
        try {
            r = f.findPath(grid, start, end, AStar2.manhattan, 1.0)

            for (v in r.path()) {
                println(v)
            }

            println(String.format("%d ms %d ops", r.duration(), r.operations()))
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }


    }
}
