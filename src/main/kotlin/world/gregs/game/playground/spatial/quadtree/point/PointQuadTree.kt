package world.gregs.game.playground.spatial.quadtree.point

import world.gregs.game.playground.spatial.quadtree.QuadTree
import java.awt.Point
import java.awt.Rectangle

/**
 * A PointQuadTree
 */
data class PointQuadTree(val boundary: Rectangle, override val capacity: Int) : QuadTree {
    val points = mutableListOf<Point>()
    var northWest: PointQuadTree? = null
    var northEast: PointQuadTree? = null
    var southWest: PointQuadTree? = null
    var southEast: PointQuadTree? = null
    val divided: Boolean
        get() = northWest != null

    override fun insert(point: Point): Boolean {
        if (!boundary.contains(point)) {
            return false
        }
        return if (points.size < capacity) {
            points.add(point)
            true
        } else {
            if (!divided) {
                subdivide()
            }
            when {
                northWest!!.insert(point) -> true
                northEast!!.insert(point) -> true
                southWest!!.insert(point) -> true
                southEast!!.insert(point) -> true
                else -> false
            }
        }
    }

    private fun subdivide() {
        northWest = PointQuadTree(
            Rectangle(boundary.x, boundary.y + boundary.height / 2, boundary.width / 2, boundary.height / 2),
            capacity
        )
        northEast = PointQuadTree(
            Rectangle(
                boundary.x + boundary.width / 2,
                boundary.y + boundary.height / 2,
                boundary.width / 2,
                boundary.height / 2
            ),
            capacity
        )
        southWest = PointQuadTree(
            Rectangle(boundary.x, boundary.y, boundary.width / 2, boundary.height / 2),
            capacity
        )
        southEast = PointQuadTree(
            Rectangle(boundary.x + boundary.width / 2, boundary.y, boundary.width / 2, boundary.height / 2),
            capacity
        )
    }

    override fun query(area: Rectangle, results: MutableList<Point>): List<Point> {
        if (!boundary.intersects(area)) {
            return results
        }
        points.forEach { point ->
            if (area.contains(point)) {
                results.add(point)
            }
        }

        if (divided) {
            northWest!!.query(area, results)
            northEast!!.query(area, results)
            southWest!!.query(area, results)
            southEast!!.query(area, results)
        }
        return results
    }
}