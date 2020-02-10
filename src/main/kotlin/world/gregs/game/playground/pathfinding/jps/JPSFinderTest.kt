package world.gregs.game.playground.pathfinding.jps

import world.gregs.game.playground.Grid
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import world.gregs.game.playground.pathfinding.jps.node.NodeState


object JPSFinderTest {

    @JvmStatic
    fun main(args: Array<String>) {

        val grid = Grid<JPSNode2>(8, 8)
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

        val f = JPSFinder()

//        val r: Result
//        try {
//            r = f.findPath(grid, start, end, euclidean, 1.0)
//
//            for (v in r.path()) {
//                System.out.println(v)
//            }
//
//            println(String.format("%d ms %d ops", r.duration(), r.operations()))
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }


    }
}
