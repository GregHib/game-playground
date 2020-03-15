package world.gregs.game.playground.pathfinding.bfs

import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.text
import world.gregs.game.playground.Direction
import world.gregs.game.playground.Grid
import world.gregs.game.playground.Node
import world.gregs.game.playground.pathfinding.jps.JPSFinder.Companion.octile
import world.gregs.game.playground.ui.zoom.GridCanvas
import world.gregs.game.playground.ui.zoom.SolidGrid
import java.awt.Rectangle
import java.util.*
import kotlin.system.measureNanoTime

class BreadthFirstSearch(private val directions: Array<Direction>) {

    fun displaySearch(canvas: GridCanvas<*, *>, start: Node) {
        val distances = searchN(canvas.grid, start)
        distances.forEachIndexed { x, it ->
            it.forEachIndexed { y, distance ->
                if(distance != -1.0) {
                    canvas.content.text("%.1f".format(distance)) {
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
        return arrayOf(emptyArray())
    }

    fun searchN(grid: Grid<*>, start: Node): Array<Array<Double>> {
        val distances = Array(grid.columns) { Array(grid.rows) { -1.0 } }
        println("BFS took ${measureNanoTime {
            val queue = LinkedList<Node>()
            queue.add(start)
            distances[start.x][start.y] = 0.0
            while (queue.isNotEmpty()) {
                val parent = queue.poll()
                directions.forEach { direction ->
                    val x = parent.x + direction.x
                    val y = parent.y + direction.y
                    if(x in grid.colIndices && y in grid.rowIndices && !blocked(grid as SolidGrid, parent.x, parent.y, direction) && distances[x][y] == -1.0) {
                        val distance = distances[parent.x][parent.y] + if(direction.x == 0 || direction.y == 0) 1.0 else 1.5
                        distances[x][y] = distance
                        queue.add(Node(x, y))
                    }
                }
            }
        }}")
        return distances
    }

    fun blocked(grid: SolidGrid, x: Int, y: Int, direction: Direction): Boolean {
        if(grid.blocked(x + direction.x, y + direction.y)) {
            return true
        }
        if(direction.x != 0 && direction.y != 0) {
            if(grid.blocked(x + direction.x, y) || grid.blocked(x, y + direction.y)) {
                return true//Crossing corners
            }
        }
        return false
    }

    fun search(grid: Grid<*>, start: Node, bounds: Rectangle): List<Node> {
        val distances = Array(grid.columns) { Array(grid.rows) { -1 } }
        val list = mutableListOf<Node>()
        val queue = LinkedList<Node>()
        queue.add(start)
        list.add(start)
        distances[start.x][start.y] = 0
        while (queue.isNotEmpty()) {
            val parent = queue.poll()
            directions.forEach { direction ->
                val x = parent.x + direction.x
                val y = parent.y + direction.y
                if(bounds.contains(x, y) &&  !(grid as SolidGrid).blocked(x, y) && distances[x][y] == -1) {
                    val distance = distances[parent.x][parent.y] + 1
                    distances[x][y] = distance
                    queue.add(Node(x, y))
                    list.add(Node(x, y))
                }
            }
        }
        return list
    }
    fun forEach(grid: Grid<*>, start: Node, bounds: Rectangle, predicate: (Int, Int, Int) -> Unit) {
        val distances = Array(grid.columns) { Array(grid.rows) { -1 } }
        val queue = LinkedList<Node>()
        queue.add(start)
        distances[start.x][start.y] = 0
        while (queue.isNotEmpty()) {
            val parent = queue.poll()
            directions.forEach { direction ->
                val x = parent.x + direction.x
                val y = parent.y + direction.y
                if(grid.inBounds(x, y) && bounds.contains(x, y) && !(grid as SolidGrid).blocked(x, y) && distances[x][y] == -1) {
                    val distance = distances[parent.x][parent.y] + 1
                    distances[x][y] = distance
                    val node = Node(x, y)
                    queue.add(node)
                    predicate(x, y, distance)
                }
            }
        }
    }
}