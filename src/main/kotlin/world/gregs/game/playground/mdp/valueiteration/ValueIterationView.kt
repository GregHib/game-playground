package world.gregs.game.playground.mdp.valueiteration

import javafx.scene.layout.Pane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.App
import tornadofx.View
import tornadofx.launch
import world.gregs.game.playground.mdp.Maze
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle

class ValueIterationView : View("Value iteration view") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
    }

    private lateinit var content: Pane

    private val maze = Maze()
    private val grid = maze.grid

    var valueIteration = ValueIteration(grid)

    override val root = grid(grid,
        PADDING, PADDING,
        1.0, 10.0
    ){
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        content.prefWidth = boundary.width.toDouble()
        content.prefHeight = boundary.height.toDouble()
        this@ValueIterationView.content = content

        updateSize()

        fun reload() {
            reloadGrid()
            maze.showGrid(this)
        }

        reload()

        setOnMouseClicked {
            GlobalScope.launch(Dispatchers.JavaFx) {
                valueIteration.start()
                while (!valueIteration.complete) {
                    valueIteration.loop()
                }
                reload()
            }
        }
    }
}

class ValueIterationApp : App(ValueIterationView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<ValueIterationApp>(*args)
}