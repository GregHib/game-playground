package world.gregs.game.playground.ui.zoom

import javafx.event.EventTarget
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import tornadofx.*
import world.gregs.game.playground.BooleanGrid
import world.gregs.game.playground.Grid

class GridCanvas<T : Any, G : Grid<T>>(
    val grid: G,
    paddingX: Double,
    paddingY: Double,
    minZoom: Double,
    maxZoom: Double
) : ZoomCanvas(paddingX, paddingY, minZoom, maxZoom) {

    val columns: Int = grid.columns
    val rows: Int = grid.rows
    var backgroundColour: Color = Color.WHITE
    var gridLineColour: Color = Color.BLACK

    lateinit var background: Rectangle

    var width: Int = 0
    var height: Int = 0
    var tileWidth: Int = 0
    var tileHeight: Int = 0

    val MouseEvent.gridX: Int
        get() = (x / tileWidth).toInt()

    val MouseEvent.gridY: Int
        get() = (yToGrid(y) / tileHeight).toInt()

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
            fill = backgroundColour
        }

        // vertical lines
        for (x in tileWidth until width step tileWidth) {
            content.line(x, 0.5, x, height - 0.5) {
                stroke = gridLineColour
            }
        }

        // horizontal lines
        for (y in tileHeight until height step tileHeight) {
            content.line(0.5, y, width - 0.5, y) {
                stroke = gridLineColour
            }
        }

        // tiles
        if (grid is SolidGrid) {
            for (x in 0 until columns) {
                for (y in 0 until rows) {
                    if (grid.blocked(x, y)) {
                        tile(x, y) {
                            fill = gridLineColour
                        }
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
        content.rectangle(gridToX(x), gridToY(y + height), width * tileWidth, height * tileHeight, op)
    }

    fun tileLine(startX: Int, startY: Int, endX: Int, endY: Int, op: Line.() -> Unit = {}) {
        content.line(gridToX(startX + 0.5), gridToY(startY + 0.5), gridToX(endX + 0.5), gridToY(endY + 0.5), op)
    }

    fun line(startX: Int, startY: Int, endX: Int, endY: Int, op: Line.() -> Unit = {}) {
        content.line(gridToX(startX), gridToY(startY), gridToX(endX), gridToY(endY), op)
    }

    fun tileText(startX: Int, startY: Int, value: String, op: Text.() -> Unit = {}) {
        content.text(value) {
            textAlignment = TextAlignment.CENTER
            this.x = gridToX(startX + 0.5) - (boundsInLocal.width / 2)
            this.y = gridToY(startY + 0.5) + (boundsInLocal.height / 4)
            op.invoke(this)
        }
    }

    fun tileText(startX: Int, startY: Int, endX: Int, endY: Int, value: String, op: Text.() -> Unit = {}) {
        content.text(value) {
            textAlignment = TextAlignment.CENTER
            this.x = gridToX(endX) - (boundsInLocal.width / 2) + (gridToX(startX) - gridToX(endX)) / 2.0
            this.y = gridToY(endY + 0.5) + (boundsInLocal.height / 2) + (gridToY(startY) - gridToY(endY)) / 2.0
            op.invoke(this)
        }
    }

}

fun EventTarget.grid(
    columns: Int,
    rows: Int,
    paddingX: Double,
    paddingY: Double,
    minZoom: Double = 1.0,
    maxZoom: Double = 10.0,
    op: GridCanvas<Boolean, BooleanGrid>.() -> Unit = {}
) = opcr(this, GridCanvas(BooleanGrid(columns, rows), paddingX, paddingY, minZoom, maxZoom), op)

fun <T : Any> EventTarget.grid(
    grid: Grid<T>,
    paddingX: Double,
    paddingY: Double,
    minZoom: Double = 1.0,
    maxZoom: Double = 10.0,
    op: GridCanvas<T, Grid<T>>.() -> Unit = {}
) = opcr(this, GridCanvas(grid, paddingX, paddingY, minZoom, maxZoom), op)