package world.gregs.game.playground.pathfinding.dijkstra

import java.util.*
import kotlin.system.measureNanoTime

inline class WeightedNode(val value: Int) : Comparable<WeightedNode> {

    constructor(index: Int, cost: Int) : this(pack(index, cost))

    val index: Int
        get() = getIndex(value)
    val cost: Int
        get() = getCost(value)

    override fun compareTo(other: WeightedNode): Int {
        return cost.compareTo(other.cost)
    }

    companion object {
        fun pack(index: Int, cost: Int) = cost or (index shl 16)
        fun getCost(value: Int) = value and 0xffff
        fun getIndex(value: Int) = value shr 16
    }
}

class DijkstrasAlgorithm(private val adj: Array<IntArray>) {

    private val queue = PriorityQueue<WeightedNode>()
    private val combined = IntArray(adj.size)

    private fun reset() {
        queue.clear()
        combined.fill(WeightedNode.pack(0, 0xffff))
        combined[0] = WeightedNode.pack(0, 0)
        queue.add(WeightedNode(0, 0))
    }

    fun search(targetIndex: Int): List<Int> {
        val time = measureNanoTime {
            reset()
            while (queue.isNotEmpty()) {
                val parent = queue.poll()
                val parentIndex = parent.index
                if (parentIndex == targetIndex || parentIndex > adj.size) {
                    break
                }
                for (node in adj[parentIndex]) {
                    val cost = parent.cost + WeightedNode.getCost(node)
                    val index = WeightedNode.getIndex(node)
                    if (WeightedNode.getCost(combined[index]) > cost) {
                        queue.add(WeightedNode(index, cost))
                        combined[index] = WeightedNode.pack(parentIndex, cost)
                    }
                }
            }
        }
        println("Took ${time}ns")
        return backtrace(targetIndex)
    }

    private fun backtrace(targetIndex: Int): List<Int> {
        return if (WeightedNode.getCost(combined[targetIndex]) != 0xffff) {
            val list = mutableListOf<Int>()
            var index = targetIndex
            while (WeightedNode.getIndex(combined[index]) != index) {
                list.add(0, index)
                index = WeightedNode.getIndex(combined[index])
            }
            list.add(0, index)
            list
        } else {
            emptyList()
        }
    }
}