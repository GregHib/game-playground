package world.gregs.game.playground.spacial.graph

import org.jgrapht.Graph
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector
import org.jgrapht.alg.shortestpath.AStarShortestPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

class JGraphT {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            // constructs a directed graph with the specified vertices and edges
            val directedGraph = DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
            directedGraph.addVertex("a")
            directedGraph.addVertex("b")
            directedGraph.addVertex("c")
            directedGraph.addVertex("d")
            directedGraph.addVertex("e")
            directedGraph.addVertex("f")
            directedGraph.addVertex("g")
            directedGraph.addVertex("h")
            directedGraph.addVertex("i")
            directedGraph.addEdge("a", "b")
            directedGraph.addEdge("b", "d")
            directedGraph.addEdge("d", "c")
            directedGraph.addEdge("c", "a")
            directedGraph.addEdge("e", "d")
            directedGraph.addEdge("e", "f")
            directedGraph.addEdge("f", "g")
            directedGraph.addEdge("g", "e")
            directedGraph.addEdge("h", "e")
            directedGraph.addEdge("i", "h")

            // computes all the strongly connected components of the directed graph
            val scAlg = KosarajuStrongConnectivityInspector(directedGraph)
            val stronglyConnectedSubgraphs = scAlg.stronglyConnectedComponents

            // prints the strongly connected components
            println("Strongly connected components:")
            for (i in stronglyConnectedSubgraphs.indices) {
                println(stronglyConnectedSubgraphs[i])
            }
            println()

            // Prints the shortest path from vertex i to vertex c. This certainly
            // exists for our particular directed graph.
            println("Shortest path from i to c:")
            val dijkstraAlg = DijkstraShortestPath(directedGraph)
            val iPaths = dijkstraAlg.getPaths("i")
            println(iPaths.getPath("c").toString() + "\n")

            // Prints the shortest path from vertex c to vertex i. This path does
            // NOT exist for our particular directed graph. Hence the path is
            // empty and the variable "path"; must be null.
            println("Shortest path from c to i:")
            val cPaths = dijkstraAlg.getPaths("c")
            println(cPaths.getPath("i"))

        }
    }
}