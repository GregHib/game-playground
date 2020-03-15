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
import kotlin.system.measureNanoTime

class BidirectionalBreadthFirstSearch(private val directions: Array<Direction>) {

    fun displaySearch(
        canvas: GridCanvas<*, *>,
        start: Node,
        end: Node
    ) {
        val width = (canvas.grid as Grid<*>).columns
        val hash = searchBiN(canvas.grid, start, end)
        distances.forEachIndexed { index, hash ->
            val x = index % width
            val y = index / width
            val distance = getDistance(hash)
            if (distance != -1) {
                canvas.content.text(distance.toString()) {
                    textAlignment = TextAlignment.CENTER
                    this.x = canvas.gridToX(x + 0.5) - (boundsInLocal.width / 2)
                    this.y = canvas.gridToY(y + 0.5) + (boundsInLocal.height / 2)
                    stroke = Color.RED
                }
            }
        }
        distances.apply {
            if (hash != -1 && getIndex(hash) != -1) {
                val index = getIndex(hash)
                val otherHash = this[index]
                val dist = getResultDist(hash)
                val otherDist = getDistance(otherHash)

                val dir = getResultDir(hash)
                val direction = if(dir != -1) Direction.values()[dir].inverse() else null
                val otherDir = getDirection(otherHash)
                val otherDirection = if(otherDir != -1) Direction.values()[otherDir].inverse() else null

                val x = index % width
                val y = index / width

                val path = mutableListOf<Int>()

                if(dist > 50) {
                    if(otherDirection != null) {
                        addSteps(path, x, y, otherDirection)
                    }
                    path.add(index)
                    if(direction != null) {
                        addSteps(path,x, y, direction)
                    }
                } else if(otherDist > 50) {
                    if(direction != null) {
                        addSteps(path, x, y, direction)
                    }
                    path.add(index)
                    if(otherDirection != null) {
                        addSteps(path, x, y, otherDirection)
                    }
                }
                path.forEach {
                    val x = it % width
                    val y = it / width
                    canvas.content.text("[      ]") {
                        textAlignment = TextAlignment.CENTER
                        this.x = canvas.gridToX(x + 0.5) - (boundsInLocal.width / 2)
                        this.y = canvas.gridToY(y + 0.5) + (boundsInLocal.height / 2)
                        stroke = Color.ORANGE
                    }
                }
            }
        }
    }

    fun addSteps(path: MutableList<Int>, x: Int, y: Int, direction: Direction) {
        val x = x + direction.x
        val y = y + direction.y
        if(x in 0 until size && y in 0 until size) {
            val index = index(x, y)

            val value = distances[index]
            val distance = getDistance(value)
            if(distance > 0) {// Target will always be > 0 but start won't be
                path.add(index)
                val dir = getDirection(value)
                if(dir != -1) {
                    val direction = Direction.values()[dir].inverse()
                    addSteps(path, x, y, direction)
                }
            }
        }
    }

    fun search(grid: Grid<*>, start: Node): Array<Array<Int>> {
        return arrayOf(emptyArray())
    }

    fun result(dist: Int, index: Int, dir: Int) = (dir + 1) + ((dist + 1) shl 4) + ((index + 1) shl 18)

    fun getResultDist(value: Int) = if(value == -1) -1 else (value shr 4 and 0x3fff) - 1

    fun getIndex(value: Int) = (value shr 18 and 0x3fff) - 1

    fun getResultDir(value: Int) = (value and 0xf) - 1



    fun dist(dir: Int, distance: Int) = (dir + 1) + (distance shl 14)

    fun getDistance(hash: Int) = hash shr 14

    fun getDirection(hash: Int) = (hash and 0x3fff).toByte().toInt() - 1

    fun hash(x: Int, y: Int) = y + (x shl 14)

    fun getX(hash: Int) = hash shr 14

    fun getY(hash: Int) = hash and 0x3fff

    val size = 16
    val distances = IntArray(size * size) { -1 }
    var startDistance = 0.0
    var endDistance = 99.0

    fun index(x: Int, y: Int) = x + size * y
    val queue = IntArray(size * size * size) { -1 }

    fun isUnvisited(distance: Int) = distance == -1
    fun visitedStart(distance: Int) = distance < 50.0
    fun visitedEnd(distance: Int) = distance > 50.0
    fun getIndexDistance(index: Int) = getDistance(distances[index])

    fun updateDistance(x: Int, y: Int, dir: Int, distance: Int) {
        distances[index(x, y)] = dist(dir, distance)
    }

    fun modifyDistance(index: Int, x: Int, y: Int, dir: Int, modifier: Int) {
        val dist = getDistance(distances[index(x, y)])
        distances[index] = dist(dir, dist + modifier)
    }
    
    fun searchBiN(
        grid: Grid<*>,
        start: Node,
        end: Node
    ): Int {
        distances.fill(-1)
        queue.fill(-1)
        var startCount = 0
        var startIndex = 0
        queue[startCount++] = hash(start.x, start.y)
        updateDistance(start.x, start.y, -1, startDistance.toInt())

        var endCount = queue.size - 1
        var endIndex = queue.size - 1
        queue[endCount--] = hash(end.x, end.y)
        updateDistance(end.x, end.y, -1, endDistance.toInt())
        grid as SolidGrid
        while (startCount > startIndex || endCount < endIndex) {
            val startParent = queue[startIndex++]
            val endParent = queue[endIndex--]

            if (startParent == -1 || endParent == -1) {
                return result(0, -1, -1)
            }

            val startParentX = getX(startParent)
            val startParentY = getY(startParent)
            val endParentX = getX(endParent)
            val endParentY = getY(endParent)
            directions.forEach { direction ->

                val startX = startParentX + direction.x
                val startY = startParentY + direction.y
                if (startX in grid.colIndices && startY in grid.rowIndices) {
                    if (!blocked(grid, startParentX, startParentY, direction)) {
                        val index = index(startX, startY)
                        val distance = getIndexDistance(index)
                        if (isUnvisited(distance)) {
                            modifyDistance(index, startParentX, startParentY, direction.ordinal, 1)
                            queue[startCount++] = hash(startX, startY)
                        } else if (visitedEnd(distance)) {
                            return result(getIndexDistance(index(startParentX, startParentY)), index, direction.ordinal)
                        }
                    }
                }

                if(endParent != -1) {
                    val endX = endParentX + direction.x
                    val endY = endParentY + direction.y
                    if (endX in grid.colIndices && endY in grid.rowIndices) {
                        if (!blocked(grid, endParentX, endParentY, direction)) {
                            val index = index(endX, endY)
                            val distance = getIndexDistance(index)
                            if (isUnvisited(distance)) {
                                modifyDistance(index, endParentX, endParentY, direction.ordinal, -1)
                                queue[endCount--] = hash(endX, endY)
                            } else if (visitedStart(distance)) {
                                return result(getIndexDistance(index(endParentX, endParentY)), index, direction.ordinal)
                            }
                        }
                    }
                }
            }
        }
        return result(0, -1, -1)
    }

    fun blocked(grid: SolidGrid, x: Int, y: Int, direction: Direction): Boolean {
        if (grid.blocked(x + direction.x, y + direction.y)) {
            return true
        }
        if (direction.x != 0 && direction.y != 0) {
            if (grid.blocked(x + direction.x, y) || grid.blocked(x, y + direction.y)) {
                return true//Crossing corners
            }
        }
        return false
    }
}