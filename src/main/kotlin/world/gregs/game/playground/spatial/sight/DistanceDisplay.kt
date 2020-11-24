package world.gregs.game.playground.spatial.sight

import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.chebyshev
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.spatial.sight.Distance.getNearest
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle

/**
 * Displays the shortest distance between two rectangles
 */
class DistanceView : View("Distance view") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
    }

    private val sourceX = 15
    private val sourceY = 15
    private val sourceWidth = 4
    private val sourceHeight = 4

    private var targetX = 0
    private var targetY = 0
    private val targetWidth = 2
    private val targetHeight = 2

    override val root = grid(
        32, 32,
        PADDING,
        PADDING
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()

        fun reload() {
            reloadGrid()
            tile(sourceX, sourceY, sourceWidth, sourceHeight) {
                fill = Color.BLUE
            }
            tile(targetX, targetY, targetWidth, targetHeight) {
                fill = Color.RED
            }
            val startX = getNearest(sourceX, sourceWidth, targetX)
            val startY = getNearest(sourceY, sourceHeight, targetY)
            val endX = getNearest(targetX, targetWidth, sourceX)
            val endY = getNearest(targetY, targetHeight, sourceY)

            tileLine(startX, startY, endX, endY) {
                stroke = Color.ORANGE
            }
            val distance = chebyshev(startX, startY, endX, endY)
            tileText(startX, startY, endX, endY, distance.toString()) {
                stroke = Color.BLACK
            }
        }

        fun reload(it: MouseEvent) {
            targetX = it.gridX
            targetY = it.gridY
            reload()
        }

        reload()

        content.setOnMouseClicked {
            if (it.clickCount > 1) {
                reload(it)
            }
        }

        content.setOnMouseMoved {
            reload(it)
        }
    }
}

class DistanceApp : App(DistanceView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<DistanceApp>(*args)
}