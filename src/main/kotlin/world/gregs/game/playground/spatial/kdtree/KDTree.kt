package world.gregs.game.playground.spatial.kdtree

import java.awt.Point

class KDTree(points: List<Point>, depth: Int = 0) {

    data class KDNode(val point: Point, val left: KDNode?, val right: KDNode?)

    private fun build(points: List<Point>, depth: Int): KDNode? {

        if (points.isNotEmpty()) {
            val axis = depth % 2

            val sortedPoints = points.sortedBy { it[axis] }
            val size = points.size
            return KDNode(
                sortedPoints[size / 2],
                build(sortedPoints.subList(0, size / 2), depth + 1),
                build(sortedPoints.subList(size / 2 + 1, size), depth + 1)
            )
        }
        return null
    }

    val root = build(points, depth)

    fun naiveClosestPoint(root: KDNode?, point: Point, depth: Int = 0, best: Point? = null): Point? {
        if (root == null) {
            return best
        }

        val axis = depth % 2

        val nextBest: Point = if (best == null || point.distance(best) > point.distance(root.point)) {
            root.point
        } else {
            best
        }
        val nextBranch = if (point[axis] < root.point[axis]) {
            root.left
        } else {
            root.right
        }

        return naiveClosestPoint(nextBranch, point, depth + 1, nextBest)
    }

    private fun closerDistance(pivot: Point, p1: Point?, p2: Point?): Point? {
        if(p1 == null) {
            return p2
        }
        if(p2 == null) {
            return p1
        }

        val d1 = pivot.distance(p1)
        val d2 = pivot.distance(p2)

        return if(d1 < d2) {
            p1
        } else {
            p2
        }
    }

    @Suppress("NAME_SHADOWING")
    fun closestPoint(point: Point, root: KDNode? = this.root, depth: Int = 0): Point? {
        if (root == null) {
            return null
        }

        val axis = depth % 2
        val oppositeBranch: KDNode?
        val nextBranch: KDNode?

        if(point[axis] < root.point[axis]) {
            oppositeBranch = root.left
            nextBranch = root.right
        } else {
            oppositeBranch = root.right
            nextBranch = root.left
        }

        val best = closerDistance(point, closestPoint(point, nextBranch, depth + 1), root.point)

        if(point.distance(best) > kotlin.math.abs(point[axis] - root.point[axis])) {
            return closerDistance(point, closestPoint(point, oppositeBranch, depth + 1), best)
        }
        return best
    }

    fun closestPoint(points: List<Point>, to: Point): Point? {
        var bestDistance = -1.0
        var bestPoint: Point? = null
        points.forEach { point ->
            val distance = point.distance(to)
            if (distance > bestDistance) {
                bestDistance = distance
                bestPoint = point
            }
        }
        return bestPoint
    }
}

private operator fun Point.get(axis: Int): Int {
    return if (axis == 0) x else y
}
