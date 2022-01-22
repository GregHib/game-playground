package world.gregs.game.playground.pathfinding.floodfill

import javafx.scene.paint.Color
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.Direction
import world.gregs.game.playground.Node
import world.gregs.game.playground.chebyshev
import world.gregs.game.playground.manhattan
import world.gregs.game.playground.pathfinding.bfs.BreadthFirstSearchOld
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import java.util.*
import kotlin.random.Random


class DistanceGrid(width: Int, height: Int) {

    val grid = Array(width) { IntArray(height) { 11 } }

    fun get(x: Int, y: Int): Int = grid[x][y]

    fun contains(x: Int, y: Int): Boolean {
        return x >= 0 && x < grid.size && y >= 0 && y < grid[x].size
    }

    fun add(x: Int, y: Int) {
        grid[x][y] = 0
        Spiral.spiral(Node(x, y), 8) { node ->
            if (contains(node.x, node.y)) {
                val dist = chebyshev(x, y, node.x, node.y).toInt()
                if (dist < grid[node.x][node.y]) {
                    grid[node.x][node.y] = dist
                }
            }
        }
    }

    fun clear() {
        for (x in grid.indices) {
            for (y in grid[x].indices) {
                grid[x][y] = 11
            }
        }
    }
}

class DistanceFloodFillView : View("Distance Flood fill") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
    }

    private val distanceGrid = DistanceGrid(16, 16)

    override val root = grid(16, 16, PADDING, PADDING) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()

        fun randomise() {
//            grid.set(8, 8, true)
            grid.fillRandom(0.1)
            distanceGrid.clear()
            for (x in 0 until 16) {
                for (y in 0 until 16) {
                    if (grid.blocked(x, y)) {
                        distanceGrid.add(x, y)
                    }
                }
            }
        }

        fun reload() {
            reloadGrid()
            for (x in 0 until 16) {
                for (y in 0 until 16) {
                    tileText(x, y, distanceGrid.get(x, y).toString()) {
                        stroke = Color.RED
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

class DistanceFloodFillApp : App(DistanceFloodFillView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<DistanceFloodFillApp>(*args)
}