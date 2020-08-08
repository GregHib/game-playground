package world.gregs.game.playground.pathfinding.astar.impl4

import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic
import org.jgrapht.alg.shortestpath.AStarShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph
import world.gregs.game.playground.Grid
import world.gregs.game.playground.pathfinding.astar.impl2.AStar2
import world.gregs.game.playground.pathfinding.astar.impl3.AStar3
import world.gregs.game.playground.pathfinding.hpastar.HierarchicalAStarView
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import world.gregs.game.playground.pathfinding.jps.node.NodeState
import world.gregs.game.playground.ui.zoom.SolidGrid
import java.util.*
import kotlin.math.abs
import kotlin.system.measureNanoTime
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

object AStar4 {
    class ManhattanDistance : AStarAdmissibleHeuristic<JPSNode2> {
        override fun getCostEstimate(sourceVertex: JPSNode2, targetVertex: JPSNode2): Double {
            return (abs(sourceVertex.x - targetVertex.x) + abs(sourceVertex.y - targetVertex.y)).toDouble()
        }
    }

    @ExperimentalTime
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

        /*
            [ ][ ][ ][ ][ ][ ][ ]
            [ ][E][9][8][7][ ][ ]
            [X][X][X][X][6][ ][ ]
            [ ][ ][ ][ ][5][ ][ ]
            [ ][ ][ ][4][ ][ ][ ]
            [ ][ ][3][ ][ ][ ][ ]
            [ ][2][ ][ ][ ][ ][ ]
            [S][ ][ ][ ][ ][ ][ ]
         */

        grid.get(0, 5)?.state = NodeState.WALL
        grid.get(1, 5)?.state = NodeState.WALL
        grid.get(2, 5)?.state = NodeState.WALL
        grid.get(3, 5)?.state = NodeState.WALL
        val start = grid.get(0, 0)!!
        val end = grid.get(1, 6)!!

        val graph = SimpleWeightedGraph<JPSNode2, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
        for(x in 0 until 8) {
            for(y in 0 until 8) {
                val point = grid.get(x, y)
                if(point != null && point.state != NodeState.WALL) {
                    graph.addVertex(point)
                }
            }
        }
        for(x in 0 until 8) {
            for (y in 0 until 8) {
                val point = grid.get(x, y)
                if(point == null || point.state == NodeState.WALL) {
                    continue
                }
                val a = grid.get(x + 1, y)
                if (a != null && a.state != NodeState.WALL) {
                    graph.addEdge(a, point, DefaultWeightedEdge())
                }
                val b = grid.get(x - 1, y)
                if (b != null && b.state != NodeState.WALL) {
                    graph.addEdge(b, point, DefaultWeightedEdge())
                }
                val c = grid.get(x, y - 1)
                if (c != null && c.state != NodeState.WALL) {
                    graph.addEdge(c, point, DefaultWeightedEdge())
                }
                val d = grid.get(x, y + 1)
                if (d != null && d.state != NodeState.WALL) {
                    graph.addEdge(d, point, DefaultWeightedEdge())
                }

                if (a != null && a.state != NodeState.WALL && d != null && d.state != NodeState.WALL) {
                    val e = grid.get(x + 1, y + 1)
                    if (e != null && e.state != NodeState.WALL) {
                        graph.addEdge(e, point, DefaultWeightedEdge())
                    }
                }

                if (a != null && a.state != NodeState.WALL && c != null && c.state != NodeState.WALL) {
                    val e = grid.get(x + 1, y - 1)
                    if (e != null && e.state != NodeState.WALL) {
                        graph.addEdge(e, point, DefaultWeightedEdge())
                    }
                }

                if (b != null && b.state != NodeState.WALL && c != null && c.state != NodeState.WALL) {
                    val e = grid.get(x - 1, y - 1)
                    if (e != null && e.state != NodeState.WALL) {
                        graph.addEdge(e, point, DefaultWeightedEdge())
                    }
                }

                if (b != null && b.state != NodeState.WALL && d != null && d.state != NodeState.WALL) {
                    val e = grid.get(x - 1, y + 1)
                    if (e != null && e.state != NodeState.WALL) {
                        graph.addEdge(e, point, DefaultWeightedEdge())
                    }
                }
            }
        }
        val astar = AStarShortestPath(graph, ManhattanDistance())


        var total = 0L
        val count = 10
        repeat(count) {
            total += measureNanoTime {
                val astar = AStarShortestPath(graph, ManhattanDistance())
                astar.getPath(start, end) }
        }
        val time = measureTimedValue { astar.getPath(start, end) }
        val result = time.value

        result.vertexList.forEach {
            println(it)
        }
        println("Avg ${total / count}ns")
        println(String.format("%d ms %d ops", time.duration.toLongNanoseconds(), astar.numberOfExpandedNodes))
    }
    /*
    {x=0,y=0} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=INACTIVE PARENT={x=-1,y=-1}
    {x=1,y=1} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=INACTIVE PARENT={x=-1,y=-1}
    {x=2,y=2} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=INACTIVE PARENT={x=-1,y=-1}
    {x=3,y=3} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=INACTIVE PARENT={x=-1,y=-1}
    {x=4,y=4} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=INACTIVE PARENT={x=-1,y=-1}
    {x=4,y=5} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=INACTIVE PARENT={x=-1,y=-1}
    {x=4,y=6} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=INACTIVE PARENT={x=-1,y=-1}
    {x=3,y=6} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=INACTIVE PARENT={x=-1,y=-1}
    {x=2,y=6} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=INACTIVE PARENT={x=-1,y=-1}
    {x=1,y=6} G=0.00 H=0.00 F=0.00 STATE=EMPTY STATUS=INACTIVE PARENT={x=-1,y=-1}
    Avg 427210ns
    3,957,200 ms 23 ops
     */
}