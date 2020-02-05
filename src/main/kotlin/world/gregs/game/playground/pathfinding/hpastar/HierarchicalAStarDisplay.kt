package world.gregs.game.playground.pathfinding.hpastar

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.zoom
import java.awt.Rectangle

class HierarchicalAStarView : View("HierarchicalAStar") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val COLUMNS = 16
        const val ROWS = 16
        const val CLUSTER_SIZE = 4
    }

    private lateinit var content: Pane
    private val grid = Array(COLUMNS) { Array(ROWS) { false } }

    fun get(x: Int, y: Int): Boolean = grid.getOrNull(x)?.getOrNull(ROWS - 1 - y) ?: true

    init {
        grid[1][1] = true
        grid[2][1] = true
        grid[4][2] = true
        grid[6][1] = true
        grid[6][0] = true
        grid[6][4] = true
        grid[6][3] = true
        grid[7][3] = true
        grid[8][3] = true
        grid[10][4] = true
        grid[10][5] = true
        grid[11][4] = true
        grid[11][3] = true
        grid[11][2] = true
        grid[13][2] = true
        grid[13][1] = true
        grid[13][3] = true
        grid[14][2] = true
        grid[2][3] = true
        grid[3][4] = true
        grid[3][5] = true
        grid[3][6] = true
        grid[2][6] = true
        grid[1][6] = true
        grid[5][6] = true
        grid[5][7] = true
        grid[7][7] = true
        grid[7][8] = true
        grid[8][9] = true
        grid[9][9] = true
        grid[8][10] = true
        grid[8][11] = true
        grid[7][11] = true
        grid[9][7] = true
        grid[10][7] = true
        grid[12][7] = true
        grid[12][8] = true
        grid[13][6] = true
        grid[12][10] = true
        grid[13][10] = true
        grid[11][11] = true
        grid[11][12] = true
        grid[10][13] = true
        grid[12][14] = true
        grid[6][13] = true
        grid[6][14] = true
        grid[5][10] = true
        grid[4][10] = true
        grid[3][10] = true
        grid[3][11] = true
        grid[3][9] = true
        grid[2][13] = true
        grid[2][14] = true
        grid[2][15] = true
        grid[0][12] = true
        grid[0][11] = true
    }

    /**
     * Renders all tiles
     */
    private fun showGrid() {
        val w = boundary.width / COLUMNS
        val h = boundary.height / ROWS
        for (x in 0 until COLUMNS) {
            for (y in 0 until ROWS) {
                val cell = get(x, y)
                content.rectangle(x * w, y * h, w, h) {
                    fill = if (cell) Color.BLACK else Color.WHITE
                    stroke = Color.BLACK
                }
            }
        }
    }

    private fun showClusters() {
        val w = boundary.width / CLUSTER_SIZE
        val h = boundary.height / CLUSTER_SIZE
        for (x in w until boundary.width step w) {
            content.line(x, 0, x, boundary.height) {
                stroke = Color.ORANGE
            }
        }
        for (y in h until boundary.height step h) {
            content.line(0, y, boundary.width, y) {
                stroke = Color.ORANGE
            }
        }
    }

    private fun showInterEdges() {
        val w = boundary.width / COLUMNS
        val h = boundary.height / ROWS

        for (clusterY in 0 until ROWS step CLUSTER_SIZE) {
            for (clusterX in 0 until COLUMNS step CLUSTER_SIZE) {
                var count = 0
                for (x in 0 until CLUSTER_SIZE) {//Go one tile outside grid to draw last inter-edge
                    val free = !get(clusterX + x, clusterY) && !get(clusterX + x, clusterY - 1)
                    if (free) {
                        count++
                    }
                    if (count > 0) {
                        if(!free) {
                            content.rectangle((clusterX + x - count) * w, (clusterY - 1) * h, w * count, h * 2) {
                                fill = null
                                stroke = Color.BLUE
                            }
                            count = 0
                        }
                        if(x == CLUSTER_SIZE - 1) {
                            content.rectangle((clusterX + x - count + 1) * w, (clusterY - 1) * h, w * count, h * 2) {
                                fill = null
                                stroke = Color.BLUE
                            }
                            count = 0
                        }
                    }
                }
            }
        }
    }

    /**
     * Reloads grid
     */
    private fun reload() {
        content.clear()
        showGrid()
        showInterEdges()
        showClusters()
    }

    override val root = zoom(
        PADDING,
        PADDING, 1.0, 10.0
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        this@HierarchicalAStarView.content = content

        reload()

        content.setOnMouseClicked {
            val w = boundary.width / COLUMNS
            val h = boundary.height / ROWS
            val x = (it.x / w).toInt()
            val y = (ROWS - it.y / h).toInt()
            println("Clicked $x $y")

//            grid[x][y] = true
            reload()
        }
    }
}

class HierarchicalAStarApp : App(HierarchicalAStarView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<HierarchicalAStarApp>(*args)
}