package world.gregs.game.playground.pathfinding.bfs

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import tornadofx.*
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import java.util.*
import kotlin.random.Random

class BreadthFirstSearchView : View("Breadth first search") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val COLUMNS = 16
        const val ROWS = 16
        const val WALL_PERCENT = 0.3
    }

    private lateinit var content: Pane
    private val grid = Array(COLUMNS) { Array(ROWS) { false } }

    private lateinit var start: Node

    fun blocked(x: Int, y: Int): Boolean = grid.getOrNull(x)?.getOrNull(y) ?: true

    enum class Direction(val x: Int, val y: Int) {
        NORTH(0, 1),
        NORTH_EAST(1, 1),
        EAST(1, 0),
        SOUTH_EAST(1, -1),
        SOUTH(0, -1),
        SOUTH_WEST(-1, -1),
        WEST(-1, 0),
        NORTH_WEST(-1, 1)
    }

    private data class Node(val x: Int, val y: Int)

    init {
        randomise()
    }

    private fun randomise() {
        start = Node(Random.nextInt(0, COLUMNS), Random.nextInt(0, ROWS))
        grid.forEach { Arrays.fill(it, false) }
        for (x in 0 until COLUMNS) {
            for (y in 0 until ROWS) {
                if(x != start.x && y != start.y) {
                    grid[x][y] = Random.nextDouble() < WALL_PERCENT
                }
            }
        }
    }

    /**
     * Renders all tiles
     */
    private fun showGrid() {
        for (x in 0 until COLUMNS) {
            for (y in 0 until ROWS) {
                val cell = blocked(x, y)
                if (cell) {
                    root.tile(x, y) {
                        fill = Color.BLACK
                    }
                }
            }
        }
    }

    private fun showStart() {
        root.tile(start.x, start.y) {
            fill = Color.GREEN
        }
    }

    fun start() {
        reload()
    }

    /**
     * Reloads grid
     */
    private fun reload() {
        root.reloadGrid()
        showGrid()
        showStart()


        val distances = Array(COLUMNS) { Array(ROWS) { -1 } }
        val queue = LinkedList<Node>()
        queue.add(start)
        distances[start.x][start.y] = 0
        while (queue.isNotEmpty()) {
            val parent = queue.poll()
            Direction.values().forEach { direction ->
                val x = parent.x + direction.x
                val y = parent.y + direction.y
                if(!blocked(x, y) && distances[x][y] == -1) {
                    val distance = distances[parent.x][parent.y] + 1
                    distances[x][y] = distance
                    queue.add(Node(x, y))
                }
            }
        }
        distances.forEachIndexed { x, it ->
            it.forEachIndexed { y, distance ->
                if(distance != -1) {
                    content.text(distance.toString()) {
                        textAlignment = TextAlignment.CENTER
                        this.x = root.gridToX(x + 0.5) - (boundsInLocal.width / 2)
                        this.y = root.gridToY(y + 0.5) + (boundsInLocal.height / 2)
                        stroke = Color.RED
                    }
                }
            }
        }
    }

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
        this@BreadthFirstSearchView.content = content


        content.setOnMouseClicked {
            randomise()
            reload()
        }
    }
}

class BreadthFirstSearchApp : App(BreadthFirstSearchView::class, QuadTreeStyles::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        find<BreadthFirstSearchView>().start()
    }
}

fun main(args: Array<String>) {
    launch<BreadthFirstSearchApp>(*args)
}