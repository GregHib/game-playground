package world.gregs.game.playground.spacial.kdtree

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.zoom
import java.awt.Point
import java.awt.Rectangle
import kotlin.random.Random

/**
 */
class KDTreeView : View("KDTree") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val POINTS = 150
    }

    private lateinit var content: Pane

    private val points = mutableListOf<Point>()
    private var searchPoint = Point(Random.nextInt(boundary.width), Random.nextInt(boundary.height))

    var kdTree: KDTree

    init {
        repeat(POINTS) {
            points.add(Point(Random.nextInt(boundary.width), Random.nextInt(boundary.height)))
        }
        kdTree = KDTree(points)
    }

    /**
     * Renders all points
     */
    private fun showPoints() {
        points.forEach { point ->
            content.circle(point.x, boundary.height - point.y, 1) {
                fill = Color.WHITE
            }
        }
        content.circle(searchPoint.x, boundary.height - searchPoint.y, 1) {
            fill = Color.CYAN
        }
        //Render found nearest neighbour
        val neighbor = kdTree.closestPoint(searchPoint) ?: return
        content.circle(neighbor.x, boundary.height - neighbor.y, 1) {
            fill = Color.LIMEGREEN
        }
    }

    /**
     * Reloads quad tree grid and points
     */
    private fun reload() {
        content.clear()
        kdTree.root!!.drawCentreLine()
        showPoints()
    }

    override val root = zoom(
        PADDING,
        PADDING, 1.0, 10.0
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        this@KDTreeView.content = content

        reload()
    }


    /**
     * Draws a rectangles center lines
     */
    private fun KDTree.KDNode.drawCentreLine(previousPoint: Point? = null, depth: Int = 0) {
        val axis = depth % 2

        if (axis == 0) {
            // Vertical
            content.line(point.x, boundary.height - if(previousPoint != null && point.y > previousPoint.y) previousPoint.y else 0, point.x, boundary.height - if(previousPoint == null || point.y > previousPoint.y) boundary.height else previousPoint.y) {
                stroke = Color.RED
            }
        } else {
            //Horizontal
            content.line(if(previousPoint != null && point.x > previousPoint.x) previousPoint.x else 0, boundary.height - point.y, if(previousPoint == null || point.x > previousPoint.x) boundary.width else previousPoint.x, boundary.height -  point.y) {
                stroke = Color.BLUE
            }
        }
        left?.drawCentreLine(point, depth + 1)
        right?.drawCentreLine(point, depth + 1)
    }
}

class KDTreeApp : App(KDTreeView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<KDTreeApp>(*args)
}