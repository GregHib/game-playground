package world.gregs.game.playground.pathfinding.floodfill

import javafx.scene.paint.Color
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.Direction
import world.gregs.game.playground.Node
import world.gregs.game.playground.pathfinding.bfs.BreadthFirstSearch
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import kotlin.random.Random

class FloodFillView : View("Flood fill") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
    }

    private lateinit var start: Node
    private val bfs = BreadthFirstSearch(Direction.values())

    override val root = grid(16, 16, PADDING, PADDING) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()

        fun randomise() {
            grid.fillRandom(0.3)
            start = Node(Random.nextInt(0, columns), Random.nextInt(0, rows))
            grid.set(start.x, start.y, false)
        }

        fun reload() {
            reloadGrid()
            tile(start.x, start.y) {
                fill = Color.GREEN
            }
            bfs.displaySearch(this, start)
        }

        randomise()
        reload()

        content.setOnMouseClicked {
            randomise()
            reload()
        }
    }
}

class FloodFillApp : App(FloodFillView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<FloodFillApp>(*args)
}