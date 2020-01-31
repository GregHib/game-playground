package world.gregs.game.playground.pathfinding.astar

import world.gregs.game.playground.manhattan
import world.gregs.game.playground.pathfinding.astar.AStarView.Companion.allowDiagonals
import java.util.*
import kotlin.random.Random

class AStar(cols: Int, rows: Int) {
    val grid = Array(cols) { x -> Array(rows) { y -> AStarNode(x, y) } }
    val openSet = LinkedList<AStarNode>()
    val closedSet = LinkedList<AStarNode>()
    val path = mutableListOf<AStarNode>()

    private val start = grid[0][0]
    private val end = grid[cols - 1][rows - 1]
    var complete = false

    init {
        for (x in grid.indices) {
            for (y in grid[0].indices) {
                grid[x][y].addNeighbors(grid)
            }
        }
        reset()
        randomMap()
    }

    fun randomMap() {
        for (x in grid.indices) {
            for (y in grid[0].indices) {
                grid[x][y].wall = Random.nextDouble() < AStarView.WALL_PERCENT
            }
        }
        start.wall = false
        end.wall = false
    }

    fun reset() {
        path.clear()
        openSet.clear()
        closedSet.clear()

        openSet.push(start)

        for (x in grid.indices) {
            for (y in grid[0].indices) {
                grid[x][y].apply {
                    f = 0.0
                    g = 0.0
                    vh = 0.0
                    h = 0.0
                }
            }
        }
        complete = false
    }

    fun loop() {
        if (openSet.isEmpty()) {
            println("No solution found")
            complete = true
            return
        }
        var current: AStarNode = openSet.first
        for(i in 1 until openSet.size) {
            val open = openSet[i]
            if (open.f < current.f) {
                current = open
            }

            if(open.f == current.f) {
                if(open.g > current.g) {
                    current = open
                }

                if(!allowDiagonals) {
                    if(open.g == current.g && open.vh < current.vh) {
                        current = open
                    }
                }
            }
        }

        var temp: AStarNode? = current
        path.clear()
        path.add(current)
        while (temp?.previous != null) {
            path.add(temp.previous!!)
            temp = temp.previous
        }

        if (current == end) {
            println("Found")
            complete = true
            return
        }

        openSet.remove(current)
        closedSet.add(current)

        for (neighbor in current.neighbors) {
            if (neighbor.wall || closedSet.contains(neighbor)) {
                continue
            }
            val tempG = current.g + heuristic(neighbor, current)

            if(!openSet.contains(neighbor)) {
                openSet.addLast(neighbor)
            } else if(tempG >= neighbor.g) {
                continue
            }

            neighbor.g = tempG
            neighbor.h = heuristic(neighbor, end)
            if(!allowDiagonals) {
                neighbor.vh = neighbor.distance(end)
            }
            neighbor.f = neighbor.g + neighbor.h
            neighbor.previous = current
        }
    }

    private fun heuristic(neighbor: AStarNode, end: AStarNode): Double {
        return if(allowDiagonals) {
            manhattan(neighbor.x, neighbor.y, end.x, end.y)
        } else {
            (kotlin.math.abs(neighbor.x - end.x) + kotlin.math.abs(neighbor.y - end.y)).toDouble()
        }
    }

}