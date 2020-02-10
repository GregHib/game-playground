package world.gregs.game.playground.spacial.graph

import org.jgrapht.generate.GridGraphGenerator
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.util.SupplierUtil
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.Node
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import java.util.*



class JGraphTView : View("JGraphT algorithm") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
    }

    val graph = SimpleGraph<Boolean, DefaultEdge>(DefaultEdge::class.java)
    private fun usedMemory(): Long {
        val rt = Runtime.getRuntime()

        return rt.totalMemory() - rt.freeMemory()
    }
    fun report(refTime: Long) {
        val time = (System.currentTimeMillis() - refTime) / 1000.0
        var mem = usedMemory() / (1024.0 * 1024.0)
        mem = Math.round(mem * 100) / 100.0
        println(" (" + time + " sec, " + mem + "MB)")
    }
    val resultMap = HashMap<String, String>()
    val undirectedGridGraph = SimpleGraph(SupplierUtil.createStringSupplier(1), SupplierUtil.createStringSupplier(1), false)
    init {
        val rows = 3
        val cols = 4
        var time = System.currentTimeMillis()

        val generator = GridGraphGenerator<String, String>(rows, cols)
        undirectedGridGraph.addVertex("one")
        undirectedGridGraph.addVertex("two")
        undirectedGridGraph.addVertex("three")

        report(time)
        resultMap.clear()
        generator.generateGraph(undirectedGridGraph, resultMap)
        report(time)

        println(resultMap)
    }

    override val root = grid(32, 32,
        PADDING,
        PADDING
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()

        fun randomise() {
            grid.fillRandom(0.01)
            for(x in 0 until grid.columns) {
                for (y in 0 until grid.rows) {
                    if(grid.blocked(x, y, false)) {
                    }
                }
            }

        }

        fun reload() {
            reloadGrid()
            val nodes = mutableListOf<Node>()
            for(x in 0 until grid.columns) {
                for (y in 0 until grid.rows) {
                    if(grid.blocked(x, y, false)) {
                        nodes.add(Node(x, y))
                    }
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

class JGraphTApp : App(JGraphTView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<JGraphTApp>(*args)
}