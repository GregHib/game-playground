package world.gregs.game.playground.spatial.voronoi

import de.alsclo.voronoi.Voronoi
import de.alsclo.voronoi.graph.Edge
import de.alsclo.voronoi.graph.Point
import javafx.embed.swing.SwingFXUtils
import tornadofx.*
import world.gregs.game.playground.spatial.phtree.DistancesView
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.ZoomCanvas
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.math.round
import kotlin.random.Random

class VoronoiView : View("Voronoi Diagram") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val CLUSTER_SIZE = 10
    }
    val points = (0 until CLUSTER_SIZE).map { Point(Random.nextInt(boundary.width).toDouble(), Random.nextInt(boundary.height).toDouble()) }
    var graph = Voronoi(points)

    override val root = opcr(this, ZoomCanvas(16.0, 16.0, PADDING, PADDING)) {
        prefWidth = boundary.width + DistancesView.PADDING
        prefHeight = boundary.height + DistancesView.PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()
//        Collection<Point> points = ...
//        Voronoi voronoi = new Voronoi(points);
//        voronoi.getGraph();
//        val map = mutableMapOf<Int, MutableList<Node>>()
//        val px = IntArray(CLUSTER_SIZE) { Random.nextInt(boundary.width) }
//        val py = IntArray(CLUSTER_SIZE) { Random.nextInt(boundary.width) }
//        val colour = IntArray(CLUSTER_SIZE) { Random.nextInt(16777215) }
        val bi = BufferedImage(boundary.width, boundary.height + 32, BufferedImage.TYPE_INT_RGB)
        /*for (x in 0 until boundary.width) {
            for (y in 0 until boundary.height) {
                var n = 0
                for (i in 0 until CLUSTER_SIZE) {
                    if (squaredEuclidean(px[i], py[i], x, y) < squaredEuclidean(px[n], py[n], x, y)) {
                        n = i
                    }
                }
                bi.setRGB(x, y, colour[n])
                map.getOrPut(colour[n]) { mutableListOf() }.add(Node(x, y))
            }
        }*/
        val g = bi.createGraphics()

        fun draw() {

            val POINT_SIZE = 5.0
            val size = boundary.height
            val g2 = g as Graphics2D
            for (site in graph.getGraph().getSitePoints()) {
                g.fillOval(
                    Math.round(site.x - POINT_SIZE / 2).toInt(),
                    size - Math.round(site.y + POINT_SIZE / 2).toInt() + 32,
                    POINT_SIZE.toInt(),
                    POINT_SIZE.toInt()
                )
//            g2.drawString(String.format("%d,%d", (int)site.x, (int)site.y), (int) site.x, size - (int)site.y + 32);
            }

            graph.graph.edgeStream().filter { e: Edge -> e.a != null && e.b != null }.forEach { e: Edge ->
                val a = e.a.location
                val b = e.b.location
                g2.drawLine(a.x.toInt(), size - a.y.toInt() + 32, b.x.toInt(), size - b.y.toInt() + 32)
            }
            /*clear()
            graph.graph.sitePoints.forEach {

                val size = 5.0
                circle(it.x - size / 2, boundary.height - 32 - round(it.y + size / 2), size.toInt()) {
                    stroke = javafx.scene.paint.Color.RED
                }
            }
            graph.graph.edgeStream().filter { it.a != null && it.b != null }.forEach {
                line(it.a.location.x, boundary.height - it.a.location.y + 32, it.b.location.x, boundary.height - it.b.location.y) {
                    stroke = javafx.scene.paint.Color.WHITE
                }
            }*/
        }

        onLeftClick {
            graph = graph.relax()
            draw()
        }

        draw()
        /*for (i in 0 until CLUSTER_SIZE) {
            val list = map[colour[i]]!!
            g.color = Color.WHITE
            g.fill(Ellipse2D.Double(px[i] - 2.5, py[i] - 2.5, 5.0, 5.0))
            val averageX = list.sumOf { it.x } / list.size.toDouble()
            val averageY = list.sumOf { it.y } / list.size.toDouble()
            g.color = Color.BLACK
            g.fill(Ellipse2D.Double(averageX - 2.5, averageY - 2.5, 5.0, 5.0))
        }*/
        imageview(SwingFXUtils.toFXImage(bi, null))
    }
}

class VoronoiApp : App(VoronoiView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<VoronoiApp>(*args)
}