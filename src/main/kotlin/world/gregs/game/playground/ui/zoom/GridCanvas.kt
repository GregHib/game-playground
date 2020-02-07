package world.gregs.game.playground.ui.zoom

import javafx.event.EventTarget
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.clear
import tornadofx.line
import tornadofx.opcr
import tornadofx.rectangle

class GridCanvas(
    val columns: Int,
    val rows: Int,
    paddingX: Double,
    paddingY: Double,
    minZoom: Double,
    maxZoom: Double
) : ZoomCanvas(paddingX, paddingY, minZoom, maxZoom) {

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

        for (x in tileWidth until width step tileWidth) {
            content.line(x, 0.5, x, height - 0.5) {
                stroke = Color.BLACK
            }
        }

        for (y in tileHeight until height step tileHeight) {
            content.line(0.5, y, width - 0.5, y) {
                stroke = Color.BLACK
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
    minZoom: Double,
    maxZoom: Double,
    op: GridCanvas.() -> Unit = {}
) = opcr(this, GridCanvas(columns, rows, paddingX, paddingY, minZoom, maxZoom), op)