package world.gregs.game.playground.ui.zoom

import javafx.event.EventTarget
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.clear
import tornadofx.line
import tornadofx.opcr
import tornadofx.rectangle
import world.gregs.game.playground.Grid

class GridCanvas(
    val columns: Int,
    val rows: Int,
    paddingX: Double,
    paddingY: Double,
    minZoom: Double,
    maxZoom: Double
) : ZoomCanvas(paddingX, paddingY, minZoom, maxZoom) {

    val grid = Grid(columns, rows)
    lateinit var background: Rectangle

    var width: Int = 0
    var height: Int = 0
    var tileWidth: Int = 0
    var tileHeight: Int = 0

    fun updateSize() {
        width = content.prefWidth.toInt()
        height = content.prefHeight.toInt()
        tileWidth = width / columns
        tileHeight = height / rows
    }

    fun reloadGrid() {
        updateSize()
        content.clear()

        background = content.rectangle(0, 0, width, height) {
            fill = Color.WHITE
        }

        // vertical lines
        for (x in tileWidth until width step tileWidth) {
            content.line(x, 0.5, x, height - 0.5) {
                stroke = Color.BLACK
            }
        }

        // horizontal lines
        for (y in tileHeight until height step tileHeight) {
            content.line(0.5, y, width - 0.5, y) {
                stroke = Color.BLACK
            }
        }

        // tiles
        for (x in 0 until columns) {
            for (y in 0 until rows) {
                val tile = grid.blocked(x, y)
                if (tile) {
                    tile(x, y) {
                        fill = Color.BLACK
                    }
                }
            }
        }
    }

    fun gridToX(x: Int): Double = x * tileWidth.toDouble()

    fun gridToX(x: Double): Double = x * tileWidth

    fun gridToY(y: Int): Double = yToGrid(y * tileHeight).toDouble()

    fun gridToY(y: Double): Double = yToGrid(y * tileHeight)

    fun yToGrid(y: Double) = height - y

    fun yToGrid(y: Int) = height - y

    fun tile(x: Int, y: Int, width: Int = 1, height: Int = 1, op: Rectangle.() -> Unit = {}) {
        content.rectangle(x * tileWidth, yToGrid((y + height) * tileHeight), width * tileWidth, height * tileHeight, op)
    }

}

fun EventTarget.grid(
    columns: Int,
    rows: Int,
    paddingX: Double,
    paddingY: Double,
    minZoom: Double = 1.0,
    maxZoom: Double = 10.0,
    op: GridCanvas.() -> Unit = {}
) = opcr(this, GridCanvas(columns, rows, paddingX, paddingY, minZoom, maxZoom), op)