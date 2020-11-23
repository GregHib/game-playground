package world.gregs.game.playground.pathfinding.astar

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*
import world.gregs.game.playground.Grid
import world.gregs.game.playground.pathfinding.astar.impl.AStar
import world.gregs.game.playground.pathfinding.astar.impl.AStarNode
import world.gregs.game.playground.pathfinding.astar.impl2.AStar2
import world.gregs.game.playground.pathfinding.astar.impl3.AStar3
import world.gregs.game.playground.pathfinding.jps.Result
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import world.gregs.game.playground.pathfinding.jps.node.NodeState
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.SolidGrid
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle

/**
 * Two basic a-star path finding algorithms
 * Controls:
 *      Click - start search
 *      R - load new map
 *
 * Red = Path 1 visited
 * Orange = Path 1 to visit
 * Blue = Path 1
 * Green = Path 2
 */
class AStarView : View("AStar") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val COLUMNS = 32
        const val ROWS = 32
        const val WALL_PERCENT = 0.1
        const val canPassThroughCorners = true
        const val allowDiagonals = false
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

        grid.get(1, 6)!!.wall = true
        grid.get(2, 6)!!.wall = true
        grid.get(3, 6)!!.wall = true
        grid.get(4, 6)!!.wall = true
    }

    var aStar = AStar(grid)

    var result: Result? = null

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

        for (closed in aStar.closedSet) {
            content.rectangle(closed.x * w, boundary.height - ((closed.y + 1) * h) + (h / 2), w / 2, h / 2) {
                fill = Color.RED
                stroke = Color.BLACK
            }
        }

        for (open in aStar.openSet) {
            content.rectangle(open.x * w + (w / 2), boundary.height - ((open.y + 1) * h) + (h / 2), w / 2, h / 2) {
                fill = Color.ORANGE
                stroke = Color.BLACK
            }
        }

        result?.path()?.forEach { step ->
            content.rectangle(step.x * w, boundary.height - ((step.y + 1) * h), w / 2, h / 2) {
                fill = Color.GREEN
                stroke = Color.BLACK
            }
        }

        for (step in aStar.path) {
            content.rectangle(step.x * w + (w / 2), boundary.height - ((step.y + 1) * h), w / 2, h / 2) {
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
        this@AStarView.content = content

        reload()

        setOnMouseClicked {
            aStar.reset()

            aStar.start.wall = false
            aStar.end.wall = false
            val start = grid.get(grid.colIndices.random(), grid.rowIndices.random())!!
            val end = grid.get(grid.colIndices.random(), grid.rowIndices.random())!!
            aStar.start = start
            aStar.end = end


            val grid = object : Grid<JPSNode2>(columns, rows), SolidGrid {
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

            val start3 = grid.get(start.x, start.y)!!
            val end3 = grid.get(end.x, end.y)!!
            result = AStar3().findPath(grid, start3, end3, AStar2.manhattan, 1.0)


            GlobalScope.launch(Dispatchers.JavaFx) {
                aStar.start()
                while (!aStar.complete) {
                    aStar.loop()
                }
                reload()
            }
        }

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            if(event.code == KeyCode.R) {
                aStar.reset()
                aStar.randomMap(WALL_PERCENT)
                reload()
            }
        }
    }
}

class AStarApp : App(AStarView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<AStarApp>(*args)
}