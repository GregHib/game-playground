package world.gregs.game.playground.spacial.quadtree.point

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.zoom
import java.awt.Point
import java.awt.Rectangle
import kotlin.random.Random

/**
 * A basic interactive quadtree
 * Controls:
 *      scroll to zoom
 *      hold space + drag to pan
 *      click/drag to insert point(s)
 *      selection moves with mouse movement
 */
class PointQuadTreeView : View("PointQuadTree") {

    companion object {
        // Size must be power of two for this rendering, however not a quadtree requirement
        private val boundary = Rectangle(0, 0, 512, 512)
        const val WIDTH = 140
        const val HEIGHT = 90
        const val PADDING = 100.0
        const val POINTS = 150
        const val CAPACITY = 1
    }

    private val quadTree = PointQuadTree(
        boundary,
        CAPACITY
    )
    private lateinit var content: Pane
    private var selection = Rectangle(
        boundary.x + boundary.width / 2 - WIDTH / 2, boundary.y + boundary.height / 2 - HEIGHT / 2,
        WIDTH,
        HEIGHT
    )


    init {
        repeat(POINTS) {
            quadTree.insert(Point(Random.nextInt(boundary.width), Random.nextInt(
                boundary.height)))
        }
    }

    /**
     * Renders the quad tree grid recursively
     */
    private fun PointQuadTree.showGrid() {
        boundary.drawCentreLines()
        if (divided) {
            northWest!!.showGrid()
            northEast!!.showGrid()
            southWest!!.showGrid()
            southEast!!.showGrid()
        }
    }


    /**
     * Renders the quad tree grid and outline
     */
    private fun showGrid() {
        quadTree.showGrid()
        content.rectangle(boundary.x, boundary.y, boundary.width, boundary.height) {
            stroke = Color.WHITE
            fill = null
        }
        boundary.drawCentreLines()
    }

    /**
     * Renders all quad tree points recursively, highlighting [results]
     */
    private fun PointQuadTree.showPoints(results: List<Point>) {
        points.forEach { point ->
            content.circle(point.x, point.y, 1) {
                fill = if (results.contains(point)) Color.LIMEGREEN else Color.HOTPINK
            }
        }
        if (divided) {
            northWest!!.showPoints(results)
            northEast!!.showPoints(results)
            southWest!!.showPoints(results)
            southEast!!.showPoints(results)
        }
    }

    /**
     * Reloads quad tree grid and points
     */
    private fun reload() {
        content.clear()
        showGrid()
        val results = mutableListOf<Point>()
        quadTree.query(selection, results)
        quadTree.showPoints(results)
        //Selection grid
        content.rectangle(selection.x, selection.y, selection.width, selection.height) {
            stroke = Color.LIGHTGREEN
            fill = null
        }
    }

    override val root = zoom(
        PADDING,
        PADDING, 1.0, 10.0) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        this@PointQuadTreeView.content = content

        reload()

        // Insert point on click
        content.setOnMouseClicked {
            val point = Point(it.x.toInt(), it.y.toInt())
            if (quadTree.boundary.contains(point)) {
                quadTree.insert(point)
                reload()
            }
        }

        // Insert points on drag
        content.setOnMouseDragged {
            val point = Point(it.x.toInt(), it.y.toInt())
            if (quadTree.boundary.contains(point)) {
                quadTree.insert(point)
                reload()
            }
        }

        // Set selection to mouse position
        content.setOnMouseMoved {
            var x = it.x.toInt() - selection.width / 2
            var y = it.y.toInt() - selection.height / 2
            x = x.coerceIn(boundary.x, boundary.x + boundary.width - selection.width)
            y = y.coerceIn(boundary.y, boundary.y + boundary.height - selection.height)
            selection = Rectangle(x, y,
                WIDTH,
                HEIGHT
            )
            reload()
        }
    }


    /**
     * Draws a rectangles center lines
     */
    private fun Rectangle.drawCentreLines() {
        content.line(x + width / 2, y, x + width / 2, y + height) {
            stroke = Color.WHITE
        }
        content.line(x, y + height / 2, x + width, y + height / 2) {
            stroke = Color.WHITE
        }
    }
}

class PointQuadTreeApp : App(PointQuadTreeView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<PointQuadTreeApp>(*args)
}