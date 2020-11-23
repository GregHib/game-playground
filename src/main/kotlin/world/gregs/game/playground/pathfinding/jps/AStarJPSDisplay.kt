package world.gregs.game.playground.pathfinding.jps

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*
import world.gregs.game.playground.Grid
import world.gregs.game.playground.pathfinding.astar.impl.AStar
import world.gregs.game.playground.pathfinding.astar.impl.AStarNode
import world.gregs.game.playground.pathfinding.astar.impl2.AStar2
import world.gregs.game.playground.pathfinding.astar.impl3.AStar3
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import world.gregs.game.playground.pathfinding.jps.node.NodeState
import world.gregs.game.playground.pathfinding.jps.node.NodeStatus
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.SolidGrid
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle

/**
 * A basic a-star path finding algorithm
 * Controls:
 *      Click - start search
 *      R - load new map
 */
class AStarJPSView : View("JPS") {

    companion object {
        private val boundary = Rectangle(0, 0, 1024, 1024)
        const val PADDING = 100.0
        const val COLUMNS = 256
        const val ROWS = 256
        const val WALL_PERCENT = 0.2
        const val canPassThroughCorners = true
        const val allowDiagonals = true
    }

    private lateinit var content: Pane

    val grid: Grid<AStarNode> = object : Grid<AStarNode>(COLUMNS, ROWS), SolidGrid {
        override fun blocked(x: Int, y: Int): Boolean {
            return get(x, y)?.wall != false
        }
    }

    init {
        for (x in grid.colIndices) {
            for (y in grid.rowIndices) {
                grid.set(x, y, AStarNode(x, y))
            }
        }
    }

    var aStar = AStar(grid)

    init {
        aStar.start = grid.get(0, 0)!!
        aStar.end = grid.get(grid.columns - 1, grid.rows - 1)!!
    }

    var jpsResult: Result? = null
    var astarResult: Result? = null

    /**
     * Renders all tiles
     */
    private fun showGrid() {
        val w = boundary.width / grid.columns
        val h = boundary.height / grid.rows
        for (x in grid.colIndices) {
            for (y in grid.rowIndices) {
                val cell = grid.get(x, y)
                content.rectangle(x * w, boundary.height - ((y + 1) * h), w, h) {
                    fill = if (cell?.wall == true) Color.BLACK else Color.WHITE
                    stroke = Color.BLACK
                }
            }
        }

        jpsResult?.path()?.forEach { step ->
            content.rectangle(step.x * w, boundary.height - ((step.y + 1) * h), w / 2, h / 2) {
                fill = Color.RED
                stroke = Color.BLACK
            }
        }

        astarResult?.path()?.forEach { step ->
            content.rectangle(step.x * w + (w / 2), boundary.height - ((step.y + 1) * h), w / 2, h / 2) {
                fill = Color.GREEN
                stroke = Color.BLACK
            }
        }

        for (step in aStar.path) {
            content.rectangle(step.x * w + (w / 2), boundary.height - ((step.y + 1) * h) + h / 2, w / 2, h / 2) {
                fill = Color.BLUE
                stroke = Color.BLACK
            }
        }
    }

    /**
     * Reloads grid
     */
    private fun reload() {
        content.clear()
        showGrid()
    }

    override val root = grid(grid,
        PADDING,
        PADDING, 1.0, 10.0
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        this@AStarJPSView.content = content

        reload()

        setOnMouseClicked {
            aStar.reset()
            aStar.randomMap(WALL_PERCENT)

            aStar.start.wall = false
            aStar.end.wall = false


            val grid = object : Grid<JPSNode2>(grid.columns, grid.rows), SolidGrid {
                override fun blocked(x: Int, y: Int): Boolean {
                    return get(x, y)?.state != NodeState.EMPTY
                }
            }

            for (x in grid.colIndices) {
                for (y in grid.rowIndices) {
                    grid.set(x, y, JPSNode2(x, y))
                    if((this.grid as SolidGrid).blocked(x, y)) {
                        grid.get(x, y)?.state = NodeState.WALL
                    }
                }
            }

            val start = grid.get(aStar.start.x, aStar.start.y)!!
            val end = grid.get(aStar.end.x, aStar.end.y)!!

            jpsResult = JPSFinder().findPath(grid, start, end, AStar2.manhattan, 1.0)//Red
            for (x in grid.colIndices) {
                for (y in grid.rowIndices) {
                    grid.get(x, y)?.apply {
                        g = 0.0
                        h = 0.0
                        status = NodeStatus.INACTIVE
                    }
                }
            }
            println()
            println("JPS took ${jpsResult?.duration()}ns ${jpsResult?.operations()} ops")
            astarResult = AStar3().findPath(grid, start, end, AStar2.manhattan, 1.0)//Green
            println("A* #2 took ${astarResult?.duration()}ns ${astarResult?.operations()} ops")
            var operations = 0
            aStar.start()//Blue
            val t1 = System.nanoTime()
            while (!aStar.complete) {
                aStar.loop()
                operations++
            }
            println("A* #1 took ${System.nanoTime() - t1}ns ${operations} ops")
            reload()
        }

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            if(event.code == KeyCode.R) {
                reload()
            }
        }
    }
}

class AStarJPSApp : App(AStarJPSView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<AStarJPSApp>(*args)
}