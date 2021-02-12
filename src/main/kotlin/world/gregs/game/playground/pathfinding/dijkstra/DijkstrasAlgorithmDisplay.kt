package world.gregs.game.playground.pathfinding.dijkstra

import javafx.scene.paint.Color
import tornadofx.*
import world.gregs.game.playground.Node
import world.gregs.game.playground.euclidean
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.Styles
import world.gregs.game.playground.ui.arrow
import world.gregs.game.playground.ui.zoom.zoom
import java.awt.Rectangle

class DijkstrasAlgorithmDisplay : View("Dijkstra's algorithm") {

    companion object {
        const val PADDING = 100.0
        const val SCALE = 25.0
        private val boundary = Rectangle(0, 0, 512, 512)
    }

    private var graph = mutableMapOf<Node, Array<Node>>()
    private var elements: List<Node>
    private var results = mutableListOf<Node>()
    private var target: Node? = null

    init {
        val one = Node(0, 1)
        val two = Node(3, 0)
        val three = Node(2, 4)
        val four = Node(5, 3)
        val five = Node(4, 5)
        val six = Node(7, 4)
        val seven = Node(9, 2)
        val eight = Node(9, 6)
        graph[one] = arrayOf(two)
        graph[two] = arrayOf(one, four, seven)
        graph[three] = arrayOf(one, four, five)
        graph[four] = arrayOf(three, two)
        graph[five] = arrayOf(four, six)
        graph[six] = arrayOf(four, five, seven, eight)
        graph[seven] = arrayOf(two, six)
        graph[eight] = arrayOf(six)
        elements = graph.keys.toList()
    }

    private val dijkstra = DijkstrasAlgorithm(graph.mapValues { (key, value) -> value.map { WeightedNode.pack(elements.indexOf(it), euclidean(key.x, key.y, it.x, it.y).toInt()) }.toIntArray() }.values.toTypedArray())

    override val root = zoom(PADDING, PADDING, 1.0, 10.0) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING

        fun reload() {
            content.clear()
            // Edges
            for ((node, adjacentNodes) in graph) {
                for (adjacent in adjacentNodes) {
                    content.arrow(node.x * SCALE, boundary.height - node.y * SCALE, adjacent.x * SCALE, boundary.height - adjacent.y * SCALE)
                    content.text(euclidean(node.x, node.y, adjacent.x, adjacent.y).toInt().toString()) {
                        x = (node.x + (adjacent.x - node.x) / 2) * SCALE - (layoutBounds.width / 2)
                        y = boundary.height - ((node.y + (adjacent.y - node.y) / 2) * SCALE - (layoutBounds.height / 3))
                    }
                }
            }
            // Nodes
            for ((node, _) in graph) {
                content.circle(node.x * SCALE, boundary.height - node.y * SCALE, SCALE / 2) {
                    fill = Styles.backgroundColour
                    stroke = Color.DODGERBLUE
                }
                content.text("${node.x}, ${node.y}") {
                    x = node.x * SCALE - (layoutBounds.width / 2)
                    y = boundary.height - (node.y * SCALE - (layoutBounds.height / 3))
                }
            }
            // Search result
            if (target != null) {
                content.circle(target!!.x * SCALE, boundary.height - target!!.y * SCALE, SCALE / 2) {
                    fill = Color.TRANSPARENT
                    stroke = Color.RED
                }
            }
            if (results.isNotEmpty()) {
                var last = results.first()
                for (index in 1 until results.size) {
                    val node = results[index]
                    content.line(last.x * SCALE, boundary.height - last.y * SCALE, node.x * SCALE, boundary.height - node.y * SCALE) {
                        stroke = Color.RED
                    }
                    last = node
                }
            }
        }

        reload()

        content.setOnMouseClicked {
            val target = graph.keys.indices.random()
            this@DijkstrasAlgorithmDisplay.target = elements[target]
            results.clear()
            dijkstra.search(target).forEach {
                results.add(elements[it])
            }
            reload()
        }
    }
}

class DijkstrasAlgorithmApp : App(DijkstrasAlgorithmDisplay::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<DijkstrasAlgorithmApp>(*args)
}