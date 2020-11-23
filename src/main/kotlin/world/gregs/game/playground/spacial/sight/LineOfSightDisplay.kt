package world.gregs.game.playground.spacial.sight

import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle

class LineOfSightView : View("Line of sight view") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val VIEW_DISTANCE = 8
    }

    private val visibleTiles = mutableListOf<Pair<Int, Int>>()

    override val root = grid(
        32, 32,
        PADDING,
        PADDING
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()

        val los = LineOfSight(grid)

        fun updateLOS(startX: Int, startY: Int, range: Int) {
            visibleTiles.clear()
            for (x in (startX - range + 1).coerceAtLeast(0) until (startX + range).coerceAtMost(grid.columns)) {
                for (y in (startY - range + 1).coerceAtLeast(0) until (startY + range).coerceAtMost(grid.rows)) {
                    if (los.canSee(startX, startY, x, y)) {
                        visibleTiles.add(x to y)
                    }
                }
            }
        }

        fun randomise() {
            grid.fillRandom(0.1)
        }

        fun reload() {
            reloadGrid()
            visibleTiles.forEach { (x, y) ->
                tile(x, y) {
                    fill = Color.rgb(0, 255, 0, 0.25)
                }
            }
        }

        fun reload(it: MouseEvent) {
            updateLOS(it.gridX, it.gridY, VIEW_DISTANCE)
            reload()
            tile(it.gridX, it.gridY) {
                fill = Color.CORNFLOWERBLUE
            }
        }

        randomise()
        reload()

        content.setOnMouseClicked {
            if (it.clickCount > 1) {
                randomise()
                reload(it)
            }
        }

        content.setOnMouseMoved {
            reload(it)
        }
    }
}

class LineOfSightApp : App(LineOfSightView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<LineOfSightApp>(*args)
}