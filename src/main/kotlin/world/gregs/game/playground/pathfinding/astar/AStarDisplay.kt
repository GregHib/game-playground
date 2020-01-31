package world.gregs.game.playground.pathfinding.astar

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.zoom
import java.awt.Rectangle

/**
 * A basic a-star path finding algorithm
 * Controls:
 *      Click - start search
 *      R - load new map
 */
class AStarView : View("AStar") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val COLUMNS = 50
        const val ROWS = 50
        const val WALL_PERCENT = 0.3
        const val canPassThroughCorners = true
        const val allowDiagonals = true
    }

    private lateinit var content: Pane

    var aStar = AStar(COLUMNS, ROWS)

    /**
     * Renders all tiles
     */
    private fun showGrid() {
        val w = boundary.width / aStar.grid.size
        val h = boundary.height / aStar.grid[0].size
        for (x in aStar.grid.indices) {
            for (y in aStar.grid[0].indices) {
                val cell = aStar.grid[x][y]
                content.rectangle(x * w, y * h, w, h) {
                    fill = if (cell.wall) Color.BLACK else Color.WHITE
                    stroke = Color.BLACK
                }
            }
        }

        for (closed in aStar.closedSet) {
            content.rectangle(closed.x * w, closed.y * h, w, h) {
                fill = Color.RED
                stroke = Color.BLACK
            }
        }

        for (open in aStar.openSet) {
            content.rectangle(open.x * w, open.y * h, w, h) {
                fill = Color.GREEN
                stroke = Color.BLACK
            }
        }

        for (step in aStar.path) {
            content.rectangle(step.x * w, step.y * h, w, h) {
                fill = Color.BLUE
                stroke = Color.BLACK
            }
        }
    }

    /**
     * Reloads grid
     */
    private fun reload() {
        content.clear()
        showGrid()
    }

    override val root = zoom(
        PADDING,
        PADDING, 1.0, 10.0
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        this@AStarView.content = content

        reload()

        setOnMouseClicked {
            aStar.reset()
            reload()
            GlobalScope.launch(Dispatchers.JavaFx) {
                while (!aStar.complete) {
                    aStar.loop()
                    reload()
                    delay(10)
                }
            }
        }

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            if(event.code == KeyCode.R) {
                aStar.reset()
                aStar.randomMap()
                reload()
            }
        }
    }
}

class AStarApp : App(AStarView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<AStarApp>(*args)
}