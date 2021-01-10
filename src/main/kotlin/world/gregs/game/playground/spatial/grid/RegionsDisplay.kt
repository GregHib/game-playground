package world.gregs.game.playground.spatial.grid

import javafx.scene.paint.Color
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.Direction
import world.gregs.game.playground.Node
import world.gregs.game.playground.pathfinding.bfs.BreadthFirstSearchOld
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import java.util.*
import kotlin.random.Random

/**
 * Uses flood fill to determine distinct connected regions in a map
 */
class RegionGroupingView : View("Region grouping") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
    }

    private val bfs = BreadthFirstSearchOld(Direction.cardinal)
    var regionCount = 0
    val colours = mutableMapOf<Int, Color>()

    override val root = grid(16, 16,
        PADDING,
        PADDING
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()
        val cluster = Array(columns) { Array(rows) { -1 } }


        fun randomise() {
            grid.fillRandom(0.3)
            colours.clear()
            regionCount = 0
            cluster.forEach { Arrays.fill(it, -1) }
            val t1 = System.currentTimeMillis()
            for(x in 0 until grid.columns) {
                for(y in 0 until grid.rows) {
                    if(!grid.blocked(x, y) && cluster[x][y] == -1) {
                        val results = bfs.searchN(grid, Node(x, y))
                        for(rx in 0 until grid.columns) {
                            for (ry in 0 until grid.rows) {
                                if(results[rx][ry] > -1) {
                                    cluster[rx][ry] = regionCount
                                }
                            }
                        }
                        regionCount++
                    }
                }
            }
            println("Took ${System.currentTimeMillis() - t1}ms")
        }

        fun reload() {
            reloadGrid()
            //Regions
            for(x in 0 until grid.columns) {
                for (y in 0 until grid.rows) {
                    if(cluster[x][y] > -1) {
                        tileText(x, y, cluster[x][y].toString()) {
                            stroke = colours.getOrPut(cluster[x][y]) { Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255))}
                        }
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

class RegionsApp : App(RegionGroupingView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<RegionsApp>(*args)
}