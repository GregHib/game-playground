package world.gregs.game.playground.pathfinding.bfs

import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.text
import world.gregs.game.playground.Direction
import world.gregs.game.playground.Grid
import world.gregs.game.playground.Node
import world.gregs.game.playground.ui.zoom.GridCanvas
import world.gregs.game.playground.ui.zoom.SolidGrid
import java.util.*

class BreadthFirstSearch(private val directions: Array<Direction>) {

    fun displaySearch(canvas: GridCanvas<*, *>, start: Node) {
        val distances = search(canvas.grid, start)
        distances.forEachIndexed { x, it ->
            it.forEachIndexed { y, distance ->
                if(distance != -1) {
                    canvas.content.text(distance.toString()) {
                        textAlignment = TextAlignment.CENTER
                        this.x = canvas.gridToX(x + 0.5) - (boundsInLocal.width / 2)
                        this.y = canvas.gridToY(y + 0.5) + (boundsInLocal.height / 2)
                        stroke = Color.RED
                    }
                }
            }
        }
    }

    fun search(grid: Grid<*>, start: Node): Array<Array<Int>> {
        val distances = Array(grid.columns) { Array(grid.rows) { -1 } }
        val queue = LinkedList<Node>()
        queue.add(start)
        distances[start.x][start.y] = 0
        while (queue.isNotEmpty()) {
            val parent = queue.poll()
            directions.forEach { direction ->
                val x = parent.x + direction.x
                val y = parent.y + direction.y
                if(!(grid as SolidGrid).blocked(x, y) && distances[x][y] == -1) {
                    val distance = distances[parent.x][parent.y] + 1
                    distances[x][y] = distance
                    queue.add(Node(x, y))
                }
            }
        }
        return distances
    }
}