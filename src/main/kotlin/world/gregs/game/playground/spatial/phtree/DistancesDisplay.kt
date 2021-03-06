package world.gregs.game.playground.spatial.phtree

import ch.ethz.globis.phtree.v13.PhTree13
import javafx.scene.paint.Color
import tornadofx.*
import world.gregs.game.playground.manhattan
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import java.util.*

/**
 * Splits map into clusters and highlights the furthest points from a wall
 */
class DistancesView : View("PH-tree") {

    companion object {
        const val PADDING = 100.0
        const val CLUSTER_SIZE = 4
    }

    private val pht = PhTree13<Boolean>(2)

    override val root = grid(16, 16, PADDING, PADDING) {
        val distances = Array(columns) { Array(rows) { -1 } }

        fun randomise() {
            grid.fillRandom(0.3)
            pht.clear()
            distances.forEach { Arrays.fill(it, -1) }
            for(x in 0 until grid.columns) {
                for(y in 0 until grid.rows) {
                    if(grid.blocked(x, y)) {
                        pht.put(longArrayOf(x.toLong(), y.toLong()), true)
                    }
                }
            }
            val t1 = System.currentTimeMillis()
            for(x in 0 until grid.columns) {
                for(y in 0 until grid.rows) {
                    if(!grid.blocked(x, y)) {
                        val nn = pht.nearestNeighbour(1, x.toLong(), y.toLong())
                        val result = nn.nextKey()
                        val distance = manhattan(x, y, result[0].toInt(), result[1].toInt())
                        distances[x][y] = distance.toInt()
                    }
                }
            }
            println("Took ${System.currentTimeMillis() - t1}ms")
        }

        fun reload() {
            reloadGrid()
            //Draw clusters
            val w = CLUSTER_SIZE * tileWidth
            val h = CLUSTER_SIZE * tileHeight
            for (x in w until width step w) {
                content.line(x, 0, x, height) {
                    stroke = Color.ORANGE
                }
            }
            for (y in h until height step h) {
                content.line(0, y, width, y) {
                    stroke = Color.ORANGE
                }
            }

            //Highlight highest
            for (clusterX in 0 until grid.columns step CLUSTER_SIZE) {
                for (clusterY in 0 until grid.rows step CLUSTER_SIZE) {
                    var highestDist = -1
                    for(x in 0 until CLUSTER_SIZE) {
                        for(y in 0 until CLUSTER_SIZE) {
                            if(distances[clusterX + x][clusterY + y] > highestDist) {
                                highestDist = distances[clusterX + x][clusterY + y]
                            }
                        }
                    }
                    for(x in 0 until CLUSTER_SIZE) {
                        for(y in 0 until CLUSTER_SIZE) {
                            if(distances[clusterX + x][clusterY + y] == highestDist) {
                                tile(clusterX + x, clusterY + y) {
                                    fill = Color.GREEN
                                }
                            }
                        }
                    }
                }
            }
            //Distances
            for(x in 0 until grid.columns) {
                for (y in 0 until grid.rows) {
                    tileText(x, y, distances[x][y].toString()) {
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

class DistancesApp : App(DistancesView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<DistancesApp>(*args)
}