package world.gregs.game.playground.spatial.kdtree

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.zoom
import java.awt.Point
import java.awt.Rectangle
import java.awt.geom.Point2D
import kotlin.random.Random
import kotlin.system.measureNanoTime

/**
 * Random points stored in a kd tree finds the nearest neighbour to a random selected point.
 * Cyan point = Point to search from
 * Green point = Nearest neighbour
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
    var kdTree2: JavaKdTree
    var kdTree3: KDTree3<GeoName>

    init {
        repeat(POINTS) {
            points.add(Point(Random.nextInt(boundary.width), Random.nextInt(boundary.height)))
        }
        kdTree = KDTree(points)
        kdTree2 = JavaKdTree()
        for(point in points) {
            kdTree2.insert(object : java.awt.geom.Point2D() {
                override fun getX(): kotlin.Double {
                    return point.x.toDouble()
                }

                override fun getY(): kotlin.Double {
                    return point.y.toDouble()
                }

                override fun setLocation(x: kotlin.Double, y: kotlin.Double) {
                }

            })
        }
        kdTree3 = KDTree3(points.map { GeoName(it.x, it.y) })
    }

    /**
     * Renders all points
     */
    private fun showPoints() {
        points.forEach { point ->
            content.circle(point.x, boundary.height - point.y, 3) {
                fill = Color.WHITE
            }
//            content.text("${point.x}, ${point.y}") {
//                x = point.x.toDouble()
//                y = boundary.height - point.y.toDouble()
//            }
        }
        content.circle(searchPoint.x, boundary.height - searchPoint.y, 3) {
            fill = Color.CYAN
        }
        val radius = 100
        val searchArea = Rectangle(searchPoint.x - radius, searchPoint.y - radius, radius * 2, radius * 2)
        content.rectangle(searchArea.x, boundary.height - (radius * 2 + searchArea.y), searchArea.width, searchArea.height) {
            stroke = Color.CYAN
            fill = Color.TRANSPARENT
        }
//        content.text("${searchPoint.x}, ${searchPoint.y}") {
//            x = searchPoint.x.toDouble()
//            y = boundary.height - searchPoint.y.toDouble()
//        }
        //Render found nearest neighbour
        var neighbor: Point?
        var neighbors: List<Point>?
        println("Search...")
        println("${measureNanoTime {
                neighbor = kdTree.nearest(searchPoint) 
        }}ns")
        println("${measureNanoTime {
                neighbors = kdTree.range(searchArea) 
        }}ns")
        println("Neighbours: ${neighbors?.size}")
        val search = object : java.awt.geom.Point2D() {
            override fun getX(): kotlin.Double {
                return searchPoint.x.toDouble()
            }

            override fun getY(): kotlin.Double {
                return searchPoint.y.toDouble()
            }

            override fun setLocation(x: kotlin.Double, y: kotlin.Double) {
            }

        }
        var neighbor2: Point2D?
        println("${measureNanoTime {
                neighbor2 = kdTree2.nearest(search) 
        }}ns")
        var neighbor3: GeoName?
        val search3 = GeoName(searchPoint.x, searchPoint.y)
        println("${measureNanoTime {
                neighbor3 = kdTree3.findNearest(search3) 
        }}ns")


        if (neighbor != null) {
            content.circle(neighbor!!.x, boundary.height - neighbor!!.y, 3) {
                fill = Color.LIMEGREEN
            }
        }
        if (neighbor2 != null) {
//            content.circle(neighbor2!!.x, boundary.height - neighbor2!!.y, 3) {
//                fill = Color.DARKGREEN
//            }
        }
        println(neighbor)
        println("${neighbor3?.x} ${neighbor3?.y}")
        if (neighbor3 != null) {
            content.circle(neighbor3!!.x, boundary.height - neighbor3!!.y, 3) {
                fill = Color.RED
            }
        }
        if (neighbors != null) {
            for(neighbour in neighbors!!) {
                content.circle(neighbour.x, boundary.height - neighbour.y, 3) {
                    fill = Color.ORANGE
                }
            }
        }
    }

    private fun reload() {
        content.clear()
//        kdTree.drawCentreLine()
        showPoints()
    }

    override val root = zoom(
        PADDING,
        PADDING, 1.0, 10.0
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        this@KDTreeView.content = content

        setOnMouseClicked {
            searchPoint = Point(Random.nextInt(boundary.width), Random.nextInt(boundary.height))
            reload()
        }
        reload()
    }


    /**
     * Draws center lines
     */
    private fun KDTree.drawCentreLine(previousPoint: Point? = null, depth: Int = 0, index: Int = root) {

        fun draw(point: Point, depth: Int, index: Int) {
            val axis = depth % 2
            if (axis == 0) {
                content.line(if (previousPoint != null && point.x > previousPoint.x) previousPoint.x else 0, boundary.height - point.y, if (previousPoint == null || point.x > previousPoint.x) boundary.width else previousPoint.x, boundary.height - point.y) {
                    stroke = Color.BLUE
                }
            } else {
                content.line(point.x, boundary.height - if (previousPoint != null && point.y > previousPoint.y) previousPoint.y else 0, point.x, boundary.height - if (previousPoint == null || point.y > previousPoint.y) boundary.height else previousPoint.y) {
                    stroke = Color.RED
                }
            }
            val left = leftIndex(index)
            if (left != -1) {
                val l = elements[left]
                if (l != null) {
                    draw(l, depth + 1, left)
                }
            }

            val right = rightIndex(index)
            if (right != -1) {
                val r = elements[right]
                if (r != null) {
                    draw(r, depth + 1, right)
                }
            }
        }

        val point = elements[root] ?: return
        draw(point, depth, index)
    }
}

class KDTreeApp : App(KDTreeView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<KDTreeApp>(*args)
}