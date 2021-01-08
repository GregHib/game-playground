package world.gregs.game.playground.pathfinding.bfs

import javafx.scene.paint.Color
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.Direction
import world.gregs.game.playground.Node
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle
import kotlin.random.Random

class BreadthFirstSearchView : View("Breadth first search") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
    }

    private lateinit var start: Node
    private val collisions = object : Collisions {
        override fun blocked(fromX: Int, fromY: Int, toX: Int, toY: Int, direction: Int, z: Int): Boolean {
            return root.grid.get(toX, toY) ?: false
        }
    }

    private val bfs = UnsafeBreadthFirstSearch(collisions, UnsafeBreadthFirstSearch.getUnsafe(), 8, 8)

    override val root = grid(8, 8, PADDING, PADDING) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()

        fun randomise() {
            grid.fillRandom(0.3)
            start = Node(Random.nextInt(0, columns), Random.nextInt(0, rows))
            grid.set(start.x, start.y, false)
        }

        fun reload() {
            reloadGrid()
            for(x in 0 until columns) {
                for (y in 0 until rows) {
//                    BFS.collision[x, y] = grid.get(x, y)!!
//                    BFS2.collision[x][y] = grid.get(x, y)!!
                }
            }
//
            for(x in 0 until columns) {
                for(y in 0 until rows) {
//                    if(BFS.distances[x, y] >= 0.0) {
//                        tile(x, y) {
//                            fill = Color.rgb(128, 0, 0, 1.0)
//                        }
//                    }
//                    if(BFS2.distances[x][y] >= 0.0) {
//                        tile(x, y) {
//                            fill = Color.rgb(0, 0, 128, 0.25)
//                        }
//                    }
//                    if(BFS.distances[x, y] >= 0.0 && BFS2.distances[x][y] < 0.0) {
//                        tile(x, y) {
//                            fill = Color.rgb(128, 0, 0, 1.0)
//                        }
//                    }
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

class BreadthFirstSearchApp : App(BreadthFirstSearchView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<BreadthFirstSearchApp>(*args)
}