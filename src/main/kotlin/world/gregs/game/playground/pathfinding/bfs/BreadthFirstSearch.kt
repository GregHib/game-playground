package world.gregs.game.playground.pathfinding.bfs

import javafx.scene.paint.Color
import world.gregs.game.playground.Direction
import world.gregs.game.playground.Grid
import world.gregs.game.playground.Node
import world.gregs.game.playground.ui.zoom.GridCanvas
import world.gregs.game.playground.ui.zoom.SolidGrid
import java.awt.Rectangle
import java.util.*
import kotlin.system.measureNanoTime

class BreadthFirstSearch(private val directions: Array<Direction>) {

    fun displaySearch(canvas: GridCanvas<*, *>, start: Node) {
        val distances = searchNOptimised(canvas.grid, start)
        distances.forEachIndexed { x, it ->
            it.forEachIndexed { y, distance ->
                if(distance != -1.0) {
                    canvas.tileText(x, y, "%.1f".format(distance)) {
                        stroke = Color.RED
                    }
                }
            }
        }
    }

    fun searchN(grid: Grid<*>, start: Node): Array<Array<Double>> {
        val distances = Array(grid.columns) { Array(grid.rows) { -1.0 } }
//        println("BFS took ${measureNanoTime {
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
//        }}")
        return distances
    }

    fun getX(id: Int) = id shr 14
    fun getY(id: Int) = id and 0x3fff

    fun searchNOptimised(grid: Grid<*>, start: Node): Array<Array<Double>> {
        val distances = Array(grid.columns) { Array(grid.rows) { -1.0 } }
        val collision = Array(grid.columns) { x -> Array(grid.rows) { y -> (grid as SolidGrid).blocked(x, y) } }
        val startInt = start.pos
        val startX = start.x
        val startY = start.y
        val columns = grid.columns
        val rows = grid.rows
        val queue = IntArray(columns * rows) { -1 }
        var writeIndex = 0
        var readIndex = 0
        println("BFS took ${measureNanoTime {
                readIndex = 0
                writeIndex = 0
                queue[writeIndex++] = startInt
                distances[startX][startY] = 0.0
                while (readIndex < writeIndex) {
                    val parent = queue[readIndex++]
                    val parentX = getX(parent)
                    val parentY = getY(parent)
                    if (collision.getOrNull(parentX)?.getOrNull(parentY + 1) == false && distances[parentX][parentY + 1] == -1.0) {
                        distances[parentX][parentY + 1] = distances[parentX][parentY] + 1.0
                        queue[writeIndex++] = (parentY + 1) + (parentX shl 14)
                    }
                    if (collision.getOrNull(parentX + 1)?.getOrNull(parentY) == false && distances[parentX + 1][parentY] == -1.0) {
                        distances[parentX + 1][parentY] = distances[parentX][parentY] + 1.0
                        queue[writeIndex++] = parentY + ((parentX + 1) shl 14)
                    }
                    if (collision.getOrNull(parentX)?.getOrNull(parentY - 1) == false && distances[parentX][parentY - 1] == -1.0) {
                        distances[parentX][parentY - 1] = distances[parentX][parentY] + 1.0
                        queue[writeIndex++] = (parentY - 1) + (parentX shl 14)
                    }
                    if (collision.getOrNull(parentX - 1)?.getOrNull(parentY) == false && distances[parentX - 1][parentY] == -1.0) {
                        distances[parentX - 1][parentY] = distances[parentX][parentY] + 1.0
                        queue[writeIndex++] = parentY + ((parentX - 1) shl 14)
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