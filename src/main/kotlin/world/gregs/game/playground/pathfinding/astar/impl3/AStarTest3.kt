package world.gregs.game.playground.pathfinding.astar.impl3

import world.gregs.game.playground.Grid
import world.gregs.game.playground.pathfinding.astar.impl2.AStar2
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import world.gregs.game.playground.pathfinding.jps.node.NodeState
import world.gregs.game.playground.ui.zoom.SolidGrid
import kotlin.system.measureNanoTime

object AStarTest3 {
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
        //2012600 ms 42 ops

        grid.get(0, 5)?.state = NodeState.WALL
        grid.get(1, 5)?.state = NodeState.WALL
        grid.get(2, 5)?.state = NodeState.WALL
        grid.get(3, 5)?.state = NodeState.WALL
        val start = grid.get(0, 0)!!
        val end = grid.get(1, 6)!!

        val astar = AStar3()
        var total = 0L
        val count = 10
        repeat(count) {
            total += measureNanoTime { astar.findPath(grid, start, end, AStar2.manhattan, 1.0) }
        }
        val result = astar.findPath(grid, start, end, AStar2.manhattan, 1.0)
        for (v in result.path()) {
            println(v)
        }
        println("Avg ${total / count}ns")
        println(String.format("%d ms %d ops", result.duration(), result.operations()))
    }
}
/*
Found goal!
{x=1,y=6} G=10.66 H=0.00 F=10.66 STATE=EMPTY STATUS=OPEN PARENT={x=2,y=6}
{x=2,y=6} G=9.66 H=1.00 F=10.66 STATE=EMPTY STATUS=CLOSED PARENT={x=3,y=6}
{x=3,y=6} G=8.66 H=2.00 F=10.66 STATE=EMPTY STATUS=CLOSED PARENT={x=4,y=6}
{x=4,y=6} G=7.66 H=3.00 F=10.66 STATE=EMPTY STATUS=CLOSED PARENT={x=4,y=5}
{x=4,y=5} G=6.66 H=4.00 F=10.66 STATE=EMPTY STATUS=CLOSED PARENT={x=4,y=4}
{x=4,y=4} G=5.66 H=5.00 F=10.66 STATE=EMPTY STATUS=CLOSED PARENT={x=3,y=3}
{x=3,y=3} G=4.24 H=5.00 F=9.24 STATE=EMPTY STATUS=CLOSED PARENT={x=2,y=2}
{x=2,y=2} G=2.83 H=5.00 F=7.83 STATE=EMPTY STATUS=CLOSED PARENT={x=1,y=1}
{x=1,y=1} G=1.41 H=5.00 F=6.41 STATE=EMPTY STATUS=CLOSED PARENT={x=0,y=0}
{x=0,y=0} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=CLOSED PARENT={x=-1,y=-1}
1,708,400 ms 25 ops
 */