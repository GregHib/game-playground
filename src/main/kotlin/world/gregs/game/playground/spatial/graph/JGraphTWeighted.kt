package world.gregs.game.playground.spatial.graph

import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.*

class JGraphTWeighted {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            // constructs a directed graph with the specified vertices and edges
            val directedGraph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
            directedGraph.addVertex("a")
            directedGraph.addVertex("b")
            directedGraph.addVertex("c")
            val ab = directedGraph.addEdge("a", "b")
            directedGraph.setEdgeWeight(ab, 10.0)
            val ac = directedGraph.addEdge("a", "c")
            directedGraph.setEdgeWeight(ac, 1.0)
            val cb = directedGraph.addEdge("c", "b")
            directedGraph.setEdgeWeight(cb, 1.0)

            // computes all the strongly connected components of the directed graph
//            val scAlg = KosarajuStrongConnectivityInspector(directedGraph)
//            val stronglyConnectedSubgraphs = scAlg.stronglyConnectedComponents
//
//            // prints the strongly connected components
//            println("Strongly connected components:")
//            for (i in stronglyConnectedSubgraphs.indices) {
//                println(stronglyConnectedSubgraphs[i])
//            }
//            println()

            println("A to b: ${DijkstraShortestPath.findPathBetween(directedGraph, "a", "b")}")
            // Prints the shortest path from vertex i to vertex c. This certainly
            // exists for our particular directed graph.
            println("Shortest path from i to c:")
            val dijkstraAlg = DijkstraShortestPath(directedGraph)
            val iPaths = dijkstraAlg.getPaths("a")
            println(iPaths.getPath("b").toString() + "\n")

            // Prints the shortest path from vertex c to vertex i. This path does
            // NOT exist for our particular directed graph. Hence the path is
            // empty and the variable "path"; must be null.
            println("Shortest path from c to i:")
            val cPaths = dijkstraAlg.getPaths("c")
            println(cPaths.getPath("i"))

        }
    }
}