package world.gregs.game.playground.spatial.kdtree

import java.awt.Point
import kotlin.math.abs

class KDTree(points: List<Point>, depth: Int = 0) {

    private val children: IntArray = IntArray(points.size * 2)
    val elements: Array<Point?> = arrayOfNulls(points.size)
    private var index: Int = 0
    val root = build(points, depth)

    private fun build(points: List<Point>, depth: Int): Int {
        if (points.isEmpty()) {
            return -1
        }
        val axis = depth % 2
        val sortedPoints = points.sortedBy { it[axis] }
        val size = points.size
        val eleIndex = index
        elements[index++] = sortedPoints[size / 2]
        children[2 * eleIndex] = build(sortedPoints.subList(0, size / 2), depth + 1)
        children[2 * eleIndex + 1] = build(sortedPoints.subList(size / 2 + 1, size), depth + 1)
        return eleIndex
    }

    fun node(index: Int) = elements[index]

    fun leftIndex(index: Int) = children[2 * index]

    fun rightIndex(index: Int) = children[2 * index + 1]

    fun nearest(target: Point, index: Int = this.root, depth: Int = 0): Point? {
        if (index == -1) {
            return null
        }

        val parent = node(index) ?: return null
        val axis = depth.rem(2)
        val oppositeBranch: Int
        val nextBranch: Int

        if (target[axis] < parent[axis]) {
            oppositeBranch = rightIndex(index)
            nextBranch = leftIndex(index)
        } else {
            oppositeBranch = leftIndex(index)
            nextBranch = rightIndex(index)
        }

        val best = closest(target, nearest(target, nextBranch, depth + 1), parent)
        if (distance(target, best) > abs(target[axis] - parent[axis])) {
            return closest(target, nearest(target, oppositeBranch, depth + 1), best)
        }
        return best
    }

    private fun closest(target: Point, p1: Point?, p2: Point?): Point? {
        if (p1 == null) {
            return p2
        }

        if (p2 == null) {
            return p1
        }

        val d1 = distance(target, p1)
        val d2 = distance(target, p2)

        return if (d1 < d2) p1 else p2
    }

    private operator fun Point.get(axis: Int): Int {
        return if (axis == 0) x else y
    }

    private fun distance(first: Point, second: Point?): Double {
        return first.distance(second)
    }
}
