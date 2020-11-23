package world.gregs.game.playground.spatial.graph

import es.usc.citius.hipster.algorithm.Hipster
import es.usc.citius.hipster.graph.GraphBuilder
import es.usc.citius.hipster.graph.GraphSearchProblem
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class Hipster4J {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val graph = GraphBuilder.create<String, Double>()
                    .connect("A").to("B").withEdge(4.0)
                    .connect("A").to("C").withEdge(2.0)
                    .connect("B").to("C").withEdge(5.0)
                    .connect("B").to("D").withEdge(10.0)
                    .connect("C").to("E").withEdge(3.0)
                    .connect("D").to("F").withEdge(11.0)
                    .connect("E").to("D").withEdge(4.0)
                    .createUndirectedGraph()

// Create the search problem. For graph problems, just use
// the GraphSearchProblem util class to generate the problem with ease.

// Create the search problem. For graph problems, just use
// the GraphSearchProblem util class to generate the problem with ease.
            val p = GraphSearchProblem
                .startingFrom("A")
                .`in`(graph)
                .takeCostsFromEdges()
                .build()
            val directedGraph = DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)

// Search the shortest path from "A" to "F"

// Search the shortest path from "A" to "F"

            val result = Hipster.createAStar(p).search("F")
            println(result)
            println(Hipster.createDijkstra(p).search("F"))
        }
    }
}