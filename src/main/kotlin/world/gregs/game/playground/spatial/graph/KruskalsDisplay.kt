package world.gregs.game.playground.spatial.graph

import javafx.scene.paint.Color
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.Node
import world.gregs.game.playground.euclidean
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import java.util.*

/**
 * Minimum spanning tree algorithm for sparse objects
 * For dense graphs use Prim's instead
 */
class KruskalsView : View("Kruskals algorithm") {

    companion object {
        const val PADDING = 100.0
    }

    override val root = grid(
        32, 32,
        PADDING,
        PADDING
    ) {

        fun randomise() {
            grid.fillRandom(0.01)
        }

        fun reload() {
            reloadGrid()
            val nodes = mutableListOf<Node>()
            for (x in 0 until grid.columns) {
                for (y in 0 until grid.rows) {
                    if (grid.blocked(x, y, false)) {
                        nodes.add(Node(x, y))
                    }
                }
            }

            val graph = UWGraph(nodes.size)
            for (node1 in nodes) {
                for (node2 in nodes) {
                    val n1 = nodes.indexOf(node1)
                    val n2 = nodes.indexOf(node2)
                    if (n1 != n2 && !graph.adjacentEdges(n1).any { it.firstVertex == n2 && it.secondVertex == n1 }) {
                        graph.addEdge(n1, n2, euclidean(node1.x, node1.y, node2.x, node2.y))
                    }
                }
            }

            val edges = KruskalMST(graph).edges

            edges.forEach {
                val a = nodes[it.firstVertex]
                val b = nodes[it.secondVertex]
                tileLine(a.x, a.y, b.x, b.y) {
                    stroke = Color.RED
                }
            }
        }

        randomise()
        reload()

        content.setOnMouseClicked {
            randomise()
            reload()
        }
    }
}

class KruskalsApp : App(KruskalsView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<KruskalsApp>(*args)
}

/**
 * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
 */
private class KruskalMST(graph: UWGraph) {
    var weight: Double = 0.0
    var edges = LinkedList<Edge>()

    init {
        val pq = PriorityQueue<Edge>(graph.V, compareBy { it.weight })
        for (v in graph.vertices()) {
            for (e in graph.adjacentEdges(v)) {
                pq.add(e)
            }
        }

        val set = DisjointSet(graph.V)
        while (!pq.isEmpty()) {
            val edge = pq.poll()
            if (!set.connected(edge.firstVertex, edge.secondVertex)) {
                edges.add(edge)
                set.unionByRank(edge.firstVertex, edge.secondVertex)
                weight += edge.weight
            }
        }
    }
}

private data class Edge(val firstVertex: Int, val secondVertex: Int, val weight: Double) : Comparable<Edge> {
    override fun compareTo(other: Edge): Int {
        return weight.compareTo(other.weight)
    }
}

private class UWGraph(val V: Int) {
    var edgeCount: Int = 0
    private val adj: Array<Queue<Edge>> = Array(V) { LinkedList<Edge>() }

    fun addEdge(vertex1: Int, vertex2: Int, weight: Double) {
        val edge = Edge(vertex1, vertex2, weight)
        adj[vertex1].add(edge)
        adj[vertex2].add(edge)
        edgeCount++
    }

    fun adjacentEdges(v: Int): Collection<Edge> {
        return adj[v]
    }

    fun vertices(): IntRange = 0 until V
}

private class DisjointSet(val size: Int) {
    private val parent = IntArray(size)
    private val rank = ByteArray(size)
    var count = size
        private set

    init {
        for (i in parent.indices) {
            parent[i] = i
        }
    }

    fun connected(v: Int, w: Int): Boolean {
        return find(v) == find(w)
    }

    fun find(v: Int): Int {
        var v = v
        while (parent[v] != v) {
            parent[v] = parent[parent[v]]
            v = parent[v]
        }
        return v
    }

    fun unionBySize(first: Int, second: Int) {
        val root1 = find(first)
        val root2 = find(second)
        if (root1 == root2) {
            return
        }
        when {
            parent[root1] > parent[root2] -> parent[root2] = root1
            parent[root2] > parent[root1] -> parent[root1] = root2
            else -> {
                parent[root2]--
                parent[root1] = root2
            }
        }
        count--
    }

    fun unionByRank(first: Int, second: Int) {
        val root1 = find(first)
        val root2 = find(second)
        if (root1 == root2) {
            return
        }
        when {
            rank[root1] > rank[root2] -> parent[root2] = root1
            rank[root2] > rank[root1] -> parent[root1] = root2
            else -> {
                parent[root1] = root2
                rank[root2]++
            }
        }
        count--
    }
}
