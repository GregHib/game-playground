package world.gregs.game.playground.mdp.valueiteration

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*
import world.gregs.game.playground.Grid
import world.gregs.game.playground.Node
import world.gregs.game.playground.mdp.State
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.SolidGrid
import world.gregs.game.playground.ui.zoom.grid
import java.awt.Rectangle

class ValueIterationView : View("Value iteration view") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val COLUMNS = 10
        const val ROWS = 10
    }

    private lateinit var content: Pane

    val grid: Grid<State> = object : Grid<State>(COLUMNS, ROWS), SolidGrid {
        override fun blocked(x: Int, y: Int): Boolean {
            return get(x, y)?.isWall != false
        }
    }

    init {
        for (x in grid.colIndices) {
            for (y in grid.rowIndices) {
                grid.set(x, y, State(x, y).also {
                    it.id = y * COLUMNS + x
                    it.reward = -0.04
                })
            }
        }
        for(y in 1..9)
            grid.get(1, y)!!.setWall()
        for(x in 4..7)
            grid.get(x, 1)!!.setWall()
        for(y in 2..8)
            grid.get(7, y)!!.setWall()
        for(y in 3..8)
            grid.get(5, y)!!.setWall()
        grid.get(3, 8)!!.setWall()
        grid.get(4, 8)!!.setWall()

        grid.get(0, 9)!!.setGoal(1.0)
        grid.get(9, 9)!!.setGoal(-1.0)
        grid.get(9, 0)!!.setGoal(1.0)

        fun filter(oldState: State, newCoords: Node): State {
            return if (newCoords.x < 0
                || newCoords.y < 0
                || newCoords.x > 9
                || newCoords.y > 9)
                oldState
            else {
                val newState = grid.get(newCoords)!!
                if(newState.isWall)
                    oldState
                else
                    newState
            }
        }

        for (x in grid.colIndices) {
            for (y in grid.rowIndices) {
                val state = grid.get(x, y)!!
                for(a in state.actions){
                    state.transitions[a] = listOf(
                        0.7 to filter(state, a.getSuccessor(state.coords)),
                        0.1 to filter(state, a.opposite().getSuccessor(state.coords)),
                        0.1 to filter(state, a.left().getSuccessor(state.coords)),
                        0.1 to filter(state, a.right().getSuccessor(state.coords))
                    )
                }
            }
        }
    }

    var valueIteration = ValueIteration(grid)

    private fun showGrid(){

        val w = boundary.width / grid.columns
        val h = boundary.height / grid.rows
        for (x in grid.colIndices) {
            for (y in grid.rowIndices) {
                val cell = grid.get(x, y)!!
                val rec = content.rectangle(x * w, boundary.height - ((y + 1) * h), w, h) {
                    fill = when {
                        cell.isWall -> Color.BLACK
                        cell.isGoal -> Color.DEEPSKYBLUE
                        else -> Color.WHITE
                    }
                    stroke = Color.BLACK
                }
                if(cell.utility != 0.0) {
                    content.text("%.3f".format(cell.utility)) {
                        textAlignment = TextAlignment.CENTER
                        val negative = cell.utility < 0
                        val xOffset = if(negative) 19 else 16
                        this.x = rec.x + rec.width / 2 - xOffset
                        this.y = rec.y + rec.height / 2 + 3
                        stroke = if(negative) Color.RED else Color.FORESTGREEN
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
    }


    override val root = grid(
        COLUMNS, ROWS, PADDING, PADDING
    ){
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        this@ValueIterationView.content = content

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

private fun Grid<State>.get(node: Node): State?{
    return get(node.x, node.y)
}

private fun State.setGoal(d: Double) {
    isGoal = true
    utility = d
    reward = d
}

private fun State.setWall() {
    isGoal = true
    isWall = true
    utility = 0.0
    reward = 0.0
}

class ValueIterationApp : App(ValueIterationView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<ValueIterationApp>(*args)
}