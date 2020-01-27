package world.gregs.game.playground.spacial.quadtree.region

import world.gregs.game.playground.math.Point
import world.gregs.game.playground.math.Rectangle
import world.gregs.game.playground.spacial.quadtree.QuadTree

/**
 * A RegionQuadTree
 */
data class RegionQuadTree(val boundary: Rectangle, override val capacity: Int) : QuadTree {
    val points = mutableListOf<Point>()
    var northWest: RegionQuadTree? = null
    var northEast: RegionQuadTree? = null
    var southWest: RegionQuadTree? = null
    var southEast: RegionQuadTree? = null
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
        northWest = RegionQuadTree(
            Rectangle(boundary.x, boundary.y + boundary.height / 2, boundary.width / 2, boundary.height / 2),
            capacity
        )
        northEast = RegionQuadTree(
            Rectangle(
                boundary.x + boundary.width / 2,
                boundary.y + boundary.height / 2,
                boundary.width / 2,
                boundary.height / 2
            ),
            capacity
        )
        southWest = RegionQuadTree(
            Rectangle(boundary.x, boundary.y, boundary.width / 2, boundary.height / 2),
            capacity
        )
        southEast = RegionQuadTree(
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