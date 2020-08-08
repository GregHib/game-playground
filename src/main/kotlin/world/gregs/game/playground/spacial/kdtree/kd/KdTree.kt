package world.gregs.game.playground.spacial.kdtree.kd

import java.util.*


class KdTree {

    private var rootNode: Node<out Dimensional>? = null

    private class LocationComparator(private val mAxis: Int) : Comparator<Dimensional> {
        override fun compare(o1: Dimensional, o2: Dimensional): Int {
            return o1.coords[mAxis].compareTo(o2.coords[mAxis])
        }

    }

    private class Partition<T : Dimensional>(items: List<T>?, var left: Boolean, var mParentNode: Node<*>) : ArrayList<T>(items)

    fun buildRecursive(points: List<Dimensional>) {
        rootNode = buildRecursive(points, 0)
    }

    fun nearestNeighbour(point: DoubleArray): Node<*>? {
        checkNotNull(rootNode)
        return NearestNeighbour.nearest(rootNode, point)
    }

    fun kNearestNeighbour(
        point: DoubleArray,
        k: Int
    ): List<Node<*>?>? {
        checkNotNull(rootNode)
        return NearestNeighbour.kNearest(rootNode, point, k)
    }

    fun buildIterative(items: List<Dimensional>): Node<*>? {
        if (items.isEmpty()) {
            return null
        }
        Collections.sort(items, LocationComparator(0))
        var partitionLength = items.size
        var partitionMedianIndex = partitionLength / 2
        var partitionMedianItem = items[partitionMedianIndex]
        rootNode = Node(null, null, null, partitionMedianItem, 0)
        var partitionBeforeMedian: Partition<out Dimensional> = Partition(items.subList(0, partitionMedianIndex), true, rootNode!!)
        var partitionAfterMedian: Partition<out Dimensional> = Partition(items.subList(partitionMedianIndex + 1, partitionLength), false, rootNode!!)
        val stack: Stack<Partition<out Dimensional>> = Stack()
        stack.push(partitionBeforeMedian)
        stack.push(partitionAfterMedian)
        while (!stack.empty()) {
            val partition: Partition<out Dimensional> = stack.pop()
            if (partition.isEmpty()) {
                continue
            }
            val parentNode = partition.mParentNode
            val newDepth = parentNode.depth + 1
            if (partition.size == 1) {
                // This is a leaf
                val newItem = partition[0]
                val newNode: Node<*> =
                    Node(
                        parentNode,
                        null,
                        null,
                        newItem,
                        newDepth
                    )
                if (partition.left) {
                    parentNode.leftNode = newNode
                } else {
                    parentNode.rightNode = newNode
                }
            } else {
                Collections.sort(partition, LocationComparator(newDepth % DIMENSIONS))
                partitionLength = partition.size
                partitionMedianIndex = partitionLength / 2
                partitionMedianItem = partition[partitionMedianIndex]
                val newNode: Node<*> =
                    Node(
                        parentNode,
                        null,
                        null,
                        partitionMedianItem,
                        newDepth
                    )
                if (partition.left) {
                    parentNode.leftNode = newNode
                } else {
                    parentNode.rightNode = newNode
                }
                partitionBeforeMedian =
                    Partition(partition.subList(0, partitionMedianIndex), true, newNode)
                stack.push(partitionBeforeMedian)
                val subList: List<Dimensional> =
                    if (partitionMedianIndex + 1 < partitionLength) partition.subList(
                        partitionMedianIndex + 1,
                        partitionLength
                    ) else ArrayList()
                partitionAfterMedian = Partition(subList, false, newNode)
                stack.push(partitionAfterMedian)
            }
        }
        return rootNode
    }

    fun delete() {
        rootNode = null
    }

    private fun buildRecursive(
        items: List<Dimensional>,
        depth: Int
    ): Node<out Dimensional>? {
        if (items.isEmpty()) {
            return null
        }
        val axis = depth % DIMENSIONS
        Collections.sort(items, LocationComparator(axis))
        val length = items.size
        val medianIndex = length / 2
        val medianPoint = items[medianIndex]
        val beforeMedianPoints = items.subList(0, medianIndex)
        val leftChild =
            buildRecursive(beforeMedianPoints, depth + 1)
        val afterMedianPoints = items.subList(medianIndex + 1, length)
        val rightChild =
            if (length > 1) buildRecursive(afterMedianPoints, depth + 1) else null
        return Node(
            null,
            leftChild,
            rightChild,
            medianPoint,
            depth
        )
    }
    companion object {


        // There are 2 axis for 2-d space
        const val DIMENSIONS = 2
    }
}