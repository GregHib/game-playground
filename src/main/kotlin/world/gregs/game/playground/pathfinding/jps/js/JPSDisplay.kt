package world.gregs.game.playground.pathfinding.jps.js

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*
import world.gregs.game.playground.spacial.grid.GridSmoothingView
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import world.gregs.game.playground.ui.zoom.zoom
import java.awt.Rectangle

/**
 * Basic Jump Point Search
 * *BROKEN*
 * Green tile = start point
 * Red tile = target point
 * Blue tile = jump point
 * Black tile = blocked point
 *
 */
class JPSView : View("JPS") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val COLUMNS = 32
        const val ROWS = 32
        const val WALL_PERCENT = 0.1
        const val canPassThroughCorners = false
    }

    private lateinit var content: Pane

    override val root = grid(
        COLUMNS,
        ROWS,
        PADDING,
        PADDING
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()
        this@JPSView.content = content


        fun randomise() {
            grid.fillRandom(WALL_PERCENT)
        }

        fun reload() {
            reloadGrid()
            val array = Array(columns) { x -> Array(rows) { y -> JPSNode(x, y) } }
            for (x in 0 until columns) {
                for (y in 0 until rows) {
                    if (grid.blocked(x, y)) {
                        array[x][y].walkable = false
                    }
                }
            }
            val jps = JPS(columns, rows, 5, 10, false, false, array)
            for (step in jps.trail ?: return) {
                tile(step.x, step.y) {
                    fill = Color.BLUE
                }
            }
            tile(jps.startX, jps.startY) {
                fill = Color.GREEN
            }
            tile(jps.endX, jps.endY) {
                fill = Color.RED
            }
        }

        randomise()
        reload()

        setOnMouseClicked {
            randomise()
            reload()
        }
    }
}

class JPSApp : App(JPSView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<JPSApp>(*args)
}