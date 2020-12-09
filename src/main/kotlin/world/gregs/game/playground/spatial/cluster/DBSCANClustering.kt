package world.gregs.game.playground.spatial.cluster

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import org.apache.commons.math3.ml.clustering.Cluster
import org.apache.commons.math3.ml.clustering.DBSCANClusterer
import org.apache.commons.math3.ml.clustering.DoublePoint
import tornadofx.*
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.zoom
import java.awt.Rectangle
import java.io.*
import java.util.*
import kotlin.random.Random

/**
 * Clusters densely packed points based on [radius] proximity and [minimumPointsPerCluster]
 */
class DBSCANClusteringView : View("DBSCAN Clustering") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val POINTS = 150

        private const val radius = 30.0
        private const val minimumPointsPerCluster = 2
    }

    private lateinit var content: Pane

    private data class Point(val x: Double, val y: Double, var colour: Color)

    private val points = mutableListOf<Point>()

    fun randomise() {
        points.clear()
        repeat(POINTS) {
            points.add(Point(Random.nextDouble(boundary.width.toDouble()), Random.nextDouble(boundary.height.toDouble()), Color.WHITE))
        }
        clusterPoints()
    }

    fun clusterPoints() {
        val dbscan = DBSCANClusterer<DoublePoint>(radius, minimumPointsPerCluster)

        val clusters: List<Cluster<DoublePoint>> = dbscan.cluster(points.map { DoublePoint(doubleArrayOf(it.x, it.y)) })

        for (c in clusters) {
            val colour = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255))
            c.points.forEach { point ->
                points.firstOrNull { it.x == point.point.first() && it.y == point.point.last() }?.colour = colour
            }
        }
    }

    /**
     * Renders all points
     */
    private fun showPoints() {
        points.forEach { point ->
            content.circle(point.x, boundary.height - point.y, 3) {
                fill = point.colour
            }
            if (point.colour != Color.WHITE) {
                content.circle(point.x, boundary.height - point.y, 3 + radius) {
                    fill = Color.TRANSPARENT
                    stroke = point.colour
                }
            }
        }
    }

    private fun reload() {
        content.clear()
        showPoints()
    }

    override val root = zoom(
        PADDING,
        PADDING, 1.0, 10.0
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        this@DBSCANClusteringView.content = content

        setOnMouseClicked {
            randomise()
            reload()
        }
        randomise()
        reload()
    }
}

class DBSCANApp : App(DBSCANClusteringView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<DBSCANApp>(*args)
}