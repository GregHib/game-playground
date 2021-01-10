package world.gregs.game.playground.pathfinding.hpastar

import ch.ethz.globis.phtree.v13.PhTree13
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.stage.Stage
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic
import org.jgrapht.alg.shortestpath.AStarShortestPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph
import tornadofx.*
import world.gregs.game.playground.BooleanGrid
import world.gregs.game.playground.Direction
import world.gregs.game.playground.Node
import world.gregs.game.playground.pathfinding.bfs.BreadthFirstSearchOld
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import kotlin.math.abs
import kotlin.math.floor
import kotlin.system.measureNanoTime


class HierarchicalAStarView : View("HierarchicalAStar") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val COLUMNS = 64
        const val ROWS = 64
        const val CLUSTER_SIZE = 8
        const val WALL_PERCENT = 0.3
    }

    private lateinit var content: Pane
    private val bfs = BreadthFirstSearchOld(Direction.cardinal)
    val directedGraph = SimpleWeightedGraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
    private val pht = PhTree13<Int>(2)
    val nodes = mutableMapOf<Int, Node>()

    fun setup(grid: BooleanGrid) {
        grid.set(1, 1, true)
        grid.set(2, 1, true)
        grid.set(4, 2, true)
        grid.set(6, 1, true)
        grid.set(6, 0, true)
        grid.set(6, 4, true)
        grid.set(6, 3, true)
        grid.set(7, 3, true)
        grid.set(8, 3, true)
        grid.set(10, 4, true)
        grid.set(10, 5, true)
        grid.set(11, 4, true)
        grid.set(11, 3, true)
        grid.set(11, 2, true)
        grid.set(13, 2, true)
        grid.set(13, 1, true)
        grid.set(13, 3, true)
        grid.set(14, 2, true)
        grid.set(2, 3, true)
        grid.set(3, 4, true)
        grid.set(3, 5, true)
        grid.set(3, 6, true)
        grid.set(2, 6, true)
        grid.set(1, 6, true)
        grid.set(5, 6, true)
        grid.set(5, 7, true)
        grid.set(7, 7, true)
        grid.set(7, 8, true)
        grid.set(8, 9, true)
        grid.set(9, 9, true)
        grid.set(8, 10, true)
        grid.set(8, 11, true)
        grid.set(7, 11, true)
        grid.set(9, 7, true)
        grid.set(10, 7, true)
        grid.set(12, 7, true)
        grid.set(12, 8, true)
        grid.set(13, 6, true)
        grid.set(12, 10, true)
        grid.set(13, 10, true)
        grid.set(11, 11, true)
        grid.set(11, 12, true)
        grid.set(10, 13, true)
        grid.set(12, 14, true)
        grid.set(6, 13, true)
        grid.set(6, 14, true)
        grid.set(5, 10, true)
        grid.set(4, 10, true)
        grid.set(3, 10, true)
        grid.set(3, 11, true)
        grid.set(3, 9, true)
        grid.set(2, 13, true)
        grid.set(2, 14, true)
        grid.set(2, 15, true)
        grid.set(0, 12, true)
        grid.set(0, 11, true)
    }

    /**
     * Renders all tiles
     */
    private fun showGrid() {
        for (x in 0 until COLUMNS) {
            for (y in 0 until ROWS) {
                val cell = root.grid.blocked(x, y)
                if (cell) {
                    root.tile(x, y) {
                        fill = Color.BLACK
                    }
                }
            }
        }
    }

    private fun showClusters() {
        val w = CLUSTER_SIZE * root.tileWidth
        val h = CLUSTER_SIZE * root.tileHeight
        for (x in w until boundary.width step w) {
            content.line(x, 0, x, boundary.height) {
                stroke = Color.ORANGE
            }
        }
        for (y in h until boundary.height step h) {
            content.line(0, y, boundary.width, y) {
                stroke = Color.ORANGE
            }
        }
    }

    fun addNode(x: Int, y: Int) : Node {
        val node = Node(x, y)
        nodes[y + (x shl 14)] = node
        directedGraph.addVertex(node)
        pht.put(longArrayOf(x.toLong(), y.toLong()), y + (x shl 14))
        return node
    }

    private fun markEntrance(x: Int, y: Int, offsetX: Int, offsetY: Int) {
        val startX = root.gridToX(x + offsetX + 0.5)
        val startY = root.gridToY(y + offsetY + 0.5)
        val endX = root.gridToX(x + 0.5)
        val endY = root.gridToY(y + 0.5)
        content.circle(startX, startY, root.tileWidth / 10) {
            fill = Color.RED
        }
        content.circle(endX, endY, root.tileWidth / 10) {
            fill = Color.RED
        }
        root.tileLine(x, y, x + offsetX, y + offsetY) {
            stroke = Color.RED
        }
        val a = addNode(x, y)
        val b = addNode(x + offsetX, y + offsetY)
        directedGraph.addEdge(a, b)
        directedGraph.setEdgeWeight(a, b, 1.0)
        println("Add edge $a - $b - 1.0")
    }

    private fun showEntrances() {
        for (x in 0 until ROWS step CLUSTER_SIZE) {
            var count = 0
            for (y in 0 until COLUMNS) {
                if (y.rem(CLUSTER_SIZE) == 0) {
                    if (count > 0) {
                        markEntrance(x, floor(y - count / 2.0).toInt(), -1, 0)
                        count = 0
                    }
                }
                if (x != 0 && x.rem(CLUSTER_SIZE) == 0) {
                    val blocked = root.grid.blocked(x, y) || root.grid.blocked(x - 1, y)
                    if (blocked) {
                        if (count > 0) {
                            markEntrance(x, floor(y - count / 2.0).toInt(), -1, 0)
                            count = 0
                        }
                    } else {
                        count++
                    }
                }
            }
            if (count > 0) {
                markEntrance(x, floor(COLUMNS - count / 2.0).toInt(), -1, 0)
            }
        }

        for (y in 0 until COLUMNS step CLUSTER_SIZE) {
            var count = 0
            for (x in 0 until ROWS) {
                if (x.rem(CLUSTER_SIZE) == 0) {
                    if (count > 0) {
                        markEntrance(floor(x - count / 2.0).toInt(), y, 0, -1)
                        count = 0
                    }
                }
                if (y != 0 && y.rem(CLUSTER_SIZE) == 0) {
                    val blocked = root.grid.blocked(x, y) || root.grid.blocked(x, y - 1)
                    if (blocked) {
                        if (count > 0) {
                            markEntrance(floor(x - count / 2.0).toInt(), y, 0, -1)
                            count = 0
                        }
                    } else {
                        count++
                    }
                }
            }
            if (count > 0) {
                markEntrance(floor(ROWS - count / 2.0).toInt(), y, 0, -1)
            }
        }
    }

    private fun showLinks() {
        val grid = BooleanGrid(CLUSTER_SIZE, CLUSTER_SIZE)
        for (clusterX in 0 until root.grid.columns step CLUSTER_SIZE) {
            for (clusterY in 0 until root.grid.rows step CLUSTER_SIZE) {
                grid.clear()
                for (x in 0 until CLUSTER_SIZE) {
                    for (y in 0 until CLUSTER_SIZE) {
                        grid.set(x, y, root.grid.blocked(clusterX + x, clusterY + y))
                    }
                }
                for (x in 0 until CLUSTER_SIZE) {
                    for (y in 0 until CLUSTER_SIZE) {
                        val distances = bfs.searchN(grid, Node(x, y))
                        distances.forEachIndexed { dx, it ->
                            it.forEachIndexed { dy, distance ->
                                if(distance > 0) {
                                    val a = nodes[(clusterY + y) + ((clusterX + x) shl 14)]
                                    val b = nodes[(clusterY + dy) + ((clusterX + dx) shl 14)]
                                    if(a != null && b != null) {
                                        directedGraph.addEdge(a, b)
                                        directedGraph.setEdgeWeight(a, b, distance)
                                        root.tileLine(a.x, a.y, b.x, b.y) {
                                            stroke = Color.RED
                                        }
                                        root.text(a.x, a.y, b.x, b.y, distance.toString()) {
                                            stroke = Color.BLUE
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun start() {
//        setup(root.grid)
        reload()
    }

    /**
     * Reloads grid
     */
    private fun reload() {
        root.grid.fillRandom(WALL_PERCENT)
        root.reloadGrid()
        showGrid()
        showEntrances()
        showClusters()
        showLinks()
    }

    val path = mutableListOf<Line>()
    override val root = grid(
        COLUMNS,
        ROWS,
        PADDING,
        PADDING, 1.0, 10.0
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()
        this@HierarchicalAStarView.content = content


        content.setOnMouseClicked {
            println("Clicked ${it.gridX} ${it.gridY}")

            this@HierarchicalAStarView.path.forEach {
                this.content.children.remove(it)
            }
            this@HierarchicalAStarView.path.clear()
            val astar = AStarShortestPath(directedGraph, ManhattanDistance())
            val set = directedGraph.vertexSet()
            val source = set.random()
            val target = set.random()

            println("Source: $source Target: $target")
            println("Astar took: ${measureNanoTime {
                astar.getPath(source, target)
            }}ns")
            println("Dijkstra's: ${measureNanoTime {
                DijkstraShortestPath.findPathBetween(directedGraph, source, target)
            }}ns")

            val path = astar.getPath(source, target)
            var last: Node? = null
            path?.vertexList?.forEach { node ->
                if(last != null) {
                    tileLine(last!!.x, last!!.y, node.x, node.y) {
                        stroke = Color.CYAN
                        strokeWidth += 1.0
                        this@HierarchicalAStarView.path.add(this)
                    }
                }
                last = node
            }
            println(path)

            val nearest = pht.nearestNeighbour(1, 0, 0).next()
            println("Nearest neighbour: $nearest ${nodes[nearest]}")
        }
    }

    class ManhattanDistance : AStarAdmissibleHeuristic<Node> {
        override fun getCostEstimate(sourceVertex: Node, targetVertex: Node): Double {
            return (abs(sourceVertex.x - targetVertex.x) + abs(sourceVertex.y - targetVertex.y)).toDouble()
        }
    }
}

class HierarchicalAStarApp : App(HierarchicalAStarView::class, QuadTreeStyles::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        find<HierarchicalAStarView>().start()
    }
}

fun main(args: Array<String>) {
    launch<HierarchicalAStarApp>(*args)
}