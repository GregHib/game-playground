package world.gregs.game.playground.mdp.valueiteration

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*
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
        Maze.COLUMNS, Maze.ROWS, PADDING, PADDING
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

class ValueIterationApp : App(ValueIterationView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<ValueIterationApp>(*args)
}