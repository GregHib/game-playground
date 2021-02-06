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
import world.gregs.game.playground.euclidean
import world.gregs.game.playground.pathfinding.bfs.BreadthFirstSearch
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import kotlin.math.abs
import kotlin.math.round
import kotlin.system.measureNanoTime

class EnhancedHAStarView : View("EnhancedHAStar") {

    companion object {
        const val PADDING = 100.0
        const val COLUMNS = 16
        const val ROWS = 16
        const val CLUSTER_SIZE = 4
        const val WALL_PERCENT = 0.3
    }

    private lateinit var content: Pane
    private val bfs = BreadthFirstSearch(Direction.cardinal)
    val directedGraph = SimpleWeightedGraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
    private val pht = PhTree13<Int>(2)
    val nodes = mutableMapOf<Int, Node>()

    fun setup(grid: BooleanGrid) {
        grid.set(6, 2, true)

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
        for (x in w until root.width step w) {
            content.line(x, 0, x, root.height) {
                stroke = Color.ORANGE
            }
        }
        for (y in h until root.height step h) {
            content.line(0, y, root.width, y) {
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

    private fun markCenter(x: Int, y: Int) {
        content.circle(root.gridToX(x + 0.5), root.gridToY(y + 0.5), root.tileWidth / 10) {
            fill = Color.RED
        }
        addNode(x, y)
    }

    private fun showCenterPoints() {
        var regionCount = 0
        for (clusterX in 0 until COLUMNS step CLUSTER_SIZE) {
            for (clusterY in 0 until ROWS step CLUSTER_SIZE) {
                val bounds = Rectangle(clusterX, clusterY, CLUSTER_SIZE, CLUSTER_SIZE)
                val cluster = Array(CLUSTER_SIZE) { Array(CLUSTER_SIZE) { -1 } }
                for (x in 0 until CLUSTER_SIZE) {
                    for (y in 0 until CLUSTER_SIZE) {
                        if(cluster[x][y] == -1 && !root.grid.blocked(clusterX + x, clusterY + y)) {
                            val results = bfs.search(root.grid, Node(clusterX + x, clusterY + y), bounds)
//                            val colour = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255))
                            results.forEach {
                                cluster[it.x - clusterX][it.y - clusterY] = regionCount
//                                content.text(regionCount.toString()) {
//                                    this.x = root.gridToX(it.x + 0.5)
//                                    this.y = root.gridToY(it.y + 0.5)
//                                    stroke = colour
//                                    textAlignment = TextAlignment.CENTER
//                                }
                            }
                            val centroid = centroid(results)
                            val center = results.map { it to euclidean(it.x, it.y, centroid.x, centroid.y) }.minBy { it.second } ?: continue
                            markCenter(center.first.x, center.first.y)
                            regionCount++
                        }
                    }
                }
            }
        }
    }

    fun centroid(knots: List<Node>): Node {
        var centroidX = 0.0
        var centroidY = 0.0
        for (knot in knots) {
            centroidX += knot.x
            centroidY += knot.y
        }
        return Node(round(centroidX / knots.size).toInt(), round(centroidY / knots.size).toInt())
    }

    private fun linkPoints() {
        for (clusterX in 0 until COLUMNS step CLUSTER_SIZE) {
            for (clusterY in 0 until ROWS step CLUSTER_SIZE) {
                val bounds = Rectangle(clusterX, clusterY, CLUSTER_SIZE * 2, CLUSTER_SIZE * 2)
                val r = pht.query(longArrayOf(bounds.x.toLong(), bounds.y.toLong()), longArrayOf((bounds.x + bounds.width).toLong(), (bounds.y + bounds.height).toLong()))
                while(r.hasNext()) {
                    val node = nodes[r.next()] ?: continue
                    bfs.forEach(root.grid, node, bounds) { x, y, distance ->
                        val b = nodes[y + (x shl 14)]
                        if(b != null) {
                            root.tileLine(node.x, node.y, x, y) {
                                stroke = Color.RED
                            }
                            root.tileText(node.x, node.y, x, y, distance.toString()) {
                                stroke = Color.BLUE
                            }
                            directedGraph.addEdge(node, b)
                            directedGraph.setEdgeWeight(node, b, distance.toDouble())
                        }
                    }
                }
            }
        }
    }

    fun start() {
        setup(root.grid)
        reload()
    }

    /**
     * Reloads grid
     */
    private fun reload() {
//        root.grid.fillRandom(WALL_PERCENT)
        root.reloadGrid()
        showGrid()
        showCenterPoints()
        linkPoints()
        showClusters()
    }

    val path = mutableListOf<Line>()
    override val root = grid(
        COLUMNS,
        ROWS,
        PADDING,
        PADDING, 1.0, 10.0
    ) {
        this@EnhancedHAStarView.content = content
        updateSize()

        content.setOnMouseClicked {
            println("Clicked ${it.gridX} ${it.gridY}")

            this@EnhancedHAStarView.path.forEach {
                this.content.children.remove(it)
            }
            this@EnhancedHAStarView.path.clear()
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
                        this@EnhancedHAStarView.path.add(this)
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

class EnhancedAStarApp : App(EnhancedHAStarView::class, QuadTreeStyles::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        find<EnhancedHAStarView>().start()
    }
}

fun main(args: Array<String>) {
    launch<EnhancedAStarApp>(*args)
}