package world.gregs.game.playground.spatial.graph

import com.mxgraph.swing.mxGraphComponent
import org.jgrapht.ext.JGraphXAdapter
import org.jgrapht.generate.GridGraphGenerator
import org.jgrapht.graph.DefaultListenableGraph
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.util.SupplierUtil
import java.awt.Dimension
import java.util.*
import javax.swing.JApplet
import javax.swing.JFrame

/**
 * A demo applet that shows how to use JGraphX to visualize JGraphT graphs. Applet based on
 * JGraphAdapterDemo.
 *
 */
class JGraphXAdapterDemo : JApplet() {

    private var jgxAdapter: JGraphXAdapter<Int, Int>? = null

    override fun init() {
        val resultMap = HashMap<String, Int>()
        val undirectedGridGraph = SimpleGraph(SupplierUtil.createIntegerSupplier(), SupplierUtil.createIntegerSupplier(), false)

        val generator = GridGraphGenerator<Int, Int>(3, 4)
        generator.generateGraph(undirectedGridGraph, resultMap)
        // create a JGraphT graph
        val g = DefaultListenableGraph(undirectedGridGraph)


        // create a visualization using JGraph, via an adapter
        jgxAdapter = JGraphXAdapter(g)

        preferredSize = DEFAULT_SIZE
        val component = mxGraphComponent(jgxAdapter)
        component.isConnectable = false
        component.graph.isAllowDanglingEdges = false
        contentPane.add(component)
        resize(DEFAULT_SIZE)

        val v1 = "v1"
        val v2 = "v2"
        val v3 = "v3"
        val v4 = "v4"

        // add some sample data (graph manipulated via JGraphX)
//        g.addVertex(v1)
//        g.addVertex(v2)
//        g.addVertex(v3)
//        g.addVertex(v4)
//
//        g.addEdge(v1, v2)
//        g.addEdge(v2, v3)
//        g.addEdge(v3, v1)
//        g.addEdge(v4, v3)

        // positioning via jgraphx layouts
        val layout = mxGraphComponent(jgxAdapter)

        // center the circle
        val radius = 100.0
//        layout.x0 = DEFAULT_SIZE.width / 2.0 - radius
//        layout.y0 = DEFAULT_SIZE.height / 2.0 - radius
//        layout.radius = radius
//        layout.isMoveCircle = true
        layout.isVisible = true
        layout.size = Dimension(100, 100)

        // that's all there is to it!...
    }

    companion object {
        private val serialVersionUID = 2202072534703043194L

        private val DEFAULT_SIZE = Dimension(530, 320)

        /**
         * An alternative starting point for this demo, to also allow running this applet as an
         * application.
         *
         * @param args command line arguments
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val applet = JGraphXAdapterDemo()
            applet.init()

            val frame = JFrame()
            frame.contentPane.add(applet)
            frame.title = "JGraphT Adapter to JGraphX Demo"
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            frame.pack()
            frame.isVisible = true
        }
    }
}