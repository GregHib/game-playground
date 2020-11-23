package world.gregs.game.playground.spacial.grid

import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.paint.Color.rgb
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.euclidean
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle

/**
 * Visualises the cloud of influence of a point spread out over an area
 * Right click to add friendly influence (blue)
 * Left click to add enemy influence (red)
 * http://www.gameaipro.com/GameAIPro2/GameAIPro2_Chapter29_Escaping_the_Grid_Infinite-Resolution_Influence_Mapping.pdf
 */
class InfluenceGridView : View("Influence grid") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        private const val maxDistance = 10.0
    }

    /*
        manhattan - diamond
        chebyshev - square
        euclidean - circle
     */
    val distance: (x1: Int, y1: Int, x2: Int, y2: Int) -> Double = euclidean

    fun Array<Array<Double>>.modify(pointX: Int, pointY: Int, maxDistance: Double, modifier: (Double) -> Double) {
        for (x in indices) {
            for (y in this[0].indices) {
                val distance = distance(x, y, pointX, pointY)
                if (distance < maxDistance) {
                    this[x][y] += modifier.invoke(distance)
                    if (this[x][y] > 1.0) {
                        this[x][y] = 1.0
                    }
                    if (this[x][y] < -1.0) {
                        this[x][y] = -1.0
                    }
                }
            }
        }
    }

    override val root = grid(
        32, 32,
        PADDING,
        PADDING
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()
        backgroundColour = Color.BLACK
        gridLineColour = Color.BLACK

        val data = Array(columns) { Array(rows) { 0.0 } }

        fun reload() {
            reloadGrid()
            for (x in 0 until columns) {
                for (y in 0 until rows) {
                    val value = data[x][y]
                    if (value == 0.0) {
                        continue
                    }
                    tile(x, y) {
                        fill = if (value > 0) {
                            rgb(0, 0, 255, value)
                        } else {
                            rgb(255, 0, 0, -value)
                        }
                    }

                }
            }
        }

        reload()

        content.setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY) {
                data.modify(it.gridX, it.gridY, maxDistance) { distance -> -(1 - (distance / maxDistance)) }
            } else if (it.button == MouseButton.SECONDARY) {
                data.modify(it.gridX, it.gridY, maxDistance) { distance -> 1 - (distance / maxDistance) }
            }
            reload()
        }
    }
}

class InfluenceGridApp : App(InfluenceGridView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<InfluenceGridApp>(*args)
}