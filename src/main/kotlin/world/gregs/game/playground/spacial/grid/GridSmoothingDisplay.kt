package world.gregs.game.playground.spacial.grid

import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.Direction
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle

class GridSmoothingView : View("Grid smoothing") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
    }

    var smoothingLevel = 0

    override val root = grid(32, 32,
        PADDING,
        PADDING
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()


        fun smooth() {
            val t1 = System.currentTimeMillis()
            for(x in 0 until grid.columns) {
                for (y in 0 until grid.rows) {
                    val total = Direction.values().count { grid.blocked(x + it.x, y + it.y) }
                    if(total > 4) {
                        grid.set(x, y, true)
                    }
                }
            }
            println("Took ${System.currentTimeMillis() - t1}ms")
            smoothingLevel++
        }

        fun randomise() {
            grid.fillRandom(0.3)
        }

        fun reload() {
            reloadGrid()
        }

        randomise()
        reload()

        content.setOnMouseClicked {
            if(smoothingLevel > 5) {
                randomise()
                smoothingLevel = 0
            }
            smooth()
            reload()
        }
    }
}

class GridSmoothingApp : App(GridSmoothingView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<GridSmoothingApp>(*args)
}