package world.gregs.game.playground.mdp.policyiteration

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

class PolicyIterationView : View("Policy iteration view") {

    companion object {
        const val PADDING = 100.0
    }

    private lateinit var content: Pane
    private val maze = Maze()
    private val grid = maze.grid

    var policyIteration = PolicyIteration(grid)

    /**
     * Reloads grid
     */

    override val root = grid(grid,
        PADDING, PADDING,
        1.0, 10.0
    ){
        this@PolicyIterationView.content = content

        updateSize()

        fun reload() {
            reloadGrid()
            maze.showGrid(this)
        }

        reload()

        setOnMouseClicked {
            GlobalScope.launch(Dispatchers.JavaFx) {
                policyIteration.start()
                while (!policyIteration.complete) {
                    policyIteration.loop()
                }
                reload()
            }
        }
    }
}

class PolicyIterationApp : App(PolicyIterationView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<PolicyIterationApp>(*args)
}