package world.gregs.game.playground.spacial.kdtree.kd

import world.gregs.game.playground.spacial.kdtree.kd.KdTree.Companion.DIMENSIONS
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


object NearestNeighbour {

    private class Distance internal constructor(
        var mValue: Double,
        var mNode: Node<*>
    )

    private class MinPriorityQueue : PriorityQueue<Distance>(11, mComparator) {
        companion object {
            private val mComparator =
                Comparator<Distance> { o1, o2 -> o1.mValue.compareTo(o2.mValue) }
        }

    }

    private class MaxPriorityQueue : PriorityQueue<Distance>(11, mComparator) {
        companion object {
            private val mComparator =
                Comparator<Distance> { o1, o2 -> -1 * o1.mValue.compareTo(o2.mValue) }
        }
    }

    fun nearest(
        rootNode: Node<*>?,
        point: DoubleArray
    ): Node<*>? {
        if (rootNode == null) {
            return null
        }
        val minPriorityQueue = MinPriorityQueue()
        minPriorityQueue.add(Distance(0.0, rootNode))
        val bestDistance = Distance(Double.MAX_VALUE, rootNode)
        while (!minPriorityQueue.isEmpty()) {
            val currentDistance: Distance = minPriorityQueue.poll()
            if (currentDistance.mValue >= bestDistance.mValue) {
                return bestDistance.mNode
            }
            val currentNode = currentDistance.mNode
            val distanceFromCurrentNode = distance(point, currentNode.coords)
            if (distanceFromCurrentNode < bestDistance.mValue) {
                bestDistance.mNode = currentNode
                bestDistance.mValue = distanceFromCurrentNode
            }
            val axis: Int = currentNode.depth % DIMENSIONS
            val delta = point[axis] - currentNode.coords[axis]
            var away = currentNode.leftNode
            var near = currentNode.rightNode
            if (delta <= 0) {
                away = currentNode.rightNode
                near = currentNode.leftNode
            }
            if (away != null) {
                minPriorityQueue.add(Distance(delta, away))
            }
            if (near != null) {
                minPriorityQueue.add(Distance(0.0, near))
            }
        }
        return bestDistance.mNode
    }

    fun kNearest(
        rootNode: Node<*>?,
        point: DoubleArray,
        k: Int
    ): List<Node<*>>? {
        if (rootNode == null) {
            return null
        }
        val maxPriorityQueue = MaxPriorityQueue()
        maxPriorityQueue.add(Distance(Double.MAX_VALUE, rootNode))
        val minPriorityQueue = MinPriorityQueue()
        minPriorityQueue.add(Distance(0.0, rootNode))
        while (!minPriorityQueue.isEmpty()) {
            val currentDistance: Distance = minPriorityQueue.poll()
            if (currentDistance.mValue >= maxPriorityQueue.peek().mValue) {
                break
            }
            val currentNode = currentDistance.mNode
            val distanceFromCurrentNode = distance(point, currentNode.coords)
            val maxDistance: Double = maxPriorityQueue.peek().mValue
            if (maxPriorityQueue.size < k || distanceFromCurrentNode <= maxDistance) {
                maxPriorityQueue.add(Distance(distanceFromCurrentNode, currentNode))
                if (maxPriorityQueue.size > k) {
                    maxPriorityQueue.poll()
                }
            }
            val axis: Int = currentNode.depth % DIMENSIONS
            val delta = point[axis] - currentNode.coords[axis]
            var away = currentNode.leftNode
            var near = currentNode.rightNode
            if (delta <= 0) {
                away = currentNode.rightNode
                near = currentNode.leftNode
            }
            if (away != null) {
                minPriorityQueue.add(Distance(delta, away))
            }
            if (near != null) {
                minPriorityQueue.add(Distance(0.0, near))
            }
        }
        val result: MutableList<Node<*>> =
            ArrayList()
        for (distance in maxPriorityQueue) {
            result.add(distance.mNode)
        }
        return result
    }

    private fun distance(point1: DoubleArray, point2: DoubleArray): Double {
        return sqrt((point1[0] - point2[0]).pow(2.0) + (point1[1] - point2[1]).pow(2.0))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        /*
            [ ][ ][ ][ ][d][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][c]
            [ ][ ][ ][ ][ ][b][ ][1][ ][ ]
            [ ][ ][a][ ][ ][ ][ ][ ][ ][ ]
            [ ][ ][ ][ ][ ][ ][g][ ][ ][ ]
            [ ][ ][ ][ ][ ][ ][ ][f][ ][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][e][ ]
            [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
         */
        val kdTree = KdTree()
        val mItems = listOf(
            Item(2, 3),//a
            Item(5, 4),//b
            Item(9, 6),//c
            Item(4, 7),//d
            Item(8, 1),//e
            Item(7, 2),//f
            Item(6, 3) //g
        )

        kdTree.buildIterative(mItems)
        val nearest = kdTree.nearestNeighbour(doubleArrayOf(7.0, 5.0))
        assert(nearest!!.coords.contentEquals(doubleArrayOf(6.0, 3.0)))

        val kNearest = kdTree.kNearestNeighbour(doubleArrayOf(7.0, 5.0), 3)
        assert(kNearest!![0]!!.coords.contentEquals(doubleArrayOf(5.0, 4.0)))
        assert(kNearest[1]!!.coords.contentEquals(doubleArrayOf(6.0, 3.0)))
        assert(kNearest[2]!!.coords.contentEquals(doubleArrayOf(9.0, 6.0)))
    }
}