package world.gregs.game.playground.pathfinding.jps.js

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*
import world.gregs.game.playground.spacial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.zoom
import java.awt.Rectangle

/**
 */
class JPSView : View("JPS") {

    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
        const val COLUMNS = 50
        const val ROWS = 50
        const val WALL_PERCENT = 0.3
        const val canPassThroughCorners = true
        const val allowDiagonals = true
    }


    private lateinit var content: Pane
    var jps = JPS(COLUMNS, ROWS, 5, 10, false, false, null)

    /**
     * Renders all tiles
     */
    private fun showGrid() {
        val w = boundary.width / COLUMNS
        val h = boundary.height / ROWS
        for (x in 0 until COLUMNS) {
            for (y in 0 until ROWS) {
                val cell = jps.grid.getNode(x, y)
                content.rectangle(x * w, y * h, w, h) {
                    fill = if (cell?.walkable == false) Color.BLACK else Color.WHITE
                    stroke = Color.BLACK
                }
            }
        }
        for(step in jps.trail ?: return) {
            content.rectangle(step.x * w, step.y * h, w, h) {
                fill = Color.BLUE
                stroke = Color.BLACK
            }
        }
        content.rectangle(jps.startX * w, jps.startY * h, w, h) {
            fill = Color.GREEN
            stroke = Color.BLACK
        }
        content.rectangle(jps.endX * w, jps.endY * h, w, h) {
            fill = Color.RED
            stroke = Color.BLACK
        }
    }

    /**
     * Reloads grid
     */
    private fun reload() {
        content.clear()
        showGrid()
    }

    override val root = zoom(
        PADDING,
        PADDING, 1.0, 10.0
    ) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        this@JPSView.content = content

        reload()

        setOnMouseClicked {
            //            aStar.reset()
//            path = jps.findPathSync(start, end, true, true)
            reload()
//            GlobalScope.launch(Dispatchers.JavaFx) {
//                while (!aStar.complete) {
//                    aStar.loop()
//                    reload()
//                    delay(10)
//                }
//            }
        }
//
//        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
//            if(event.code == KeyCode.R) {
//                aStar.reset()
//                aStar.randomMap()
//                reload()
//            }
//        }
    }
}

class JPSApp : App(JPSView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    launch<JPSApp>(*args)
}