package world.gregs.game.playground.pathfinding.bfs

import javafx.scene.paint.Color
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.Direction
import world.gregs.game.playground.Node
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import kotlin.random.Random

class BidirectionalBreadthFirstSearchView : View("Bidirectional Breadth first search") {

    companion object {
        const val PADDING = 100.0
    }

    private lateinit var start: Node
    private lateinit var end: Node
    private val bbfs = BidirectionalBreadthFirstSearch(Direction.all)

    override val root = grid(16, 16, PADDING, PADDING) {

        fun randomise() {
            grid.fillRandom(0.3)
            start = Node(Random.nextInt(0, columns), Random.nextInt(0, rows))
            end = Node(Random.nextInt(0, columns), Random.nextInt(0, rows))
            grid.set(start.x, start.y, false)
            grid.set(end.x, end.y, false)
        }

        fun reload() {
            reloadGrid()
            tile(start.x, start.y) {
                fill = Color.GREEN
            }
            tile(end.x, end.y) {
                fill = Color.DARKRED
            }
            bbfs.displaySearch(this, start, end)
        }

        randomise()
        reload()

        content.setOnMouseClicked {
            randomise()
            reload()
        }
    }
}

class BidirectionalBreadthFirstSearchApp : App(BidirectionalBreadthFirstSearchView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<BidirectionalBreadthFirstSearchApp>(*args)
}