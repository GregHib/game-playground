package world.gregs.game.playground.pathfinding.astar.impl

import world.gregs.game.playground.Grid
import world.gregs.game.playground.ui.zoom.SolidGrid

object AStarTest {
    @JvmStatic
    fun main(args: Array<String>) {


        val grid = object : Grid<AStarNode>(8, 8), SolidGrid {
            override fun blocked(x: Int, y: Int): Boolean {
                return get(x, y)?.wall != false
            }
        }

        for (x in grid.colIndices) {
            for (y in grid.rowIndices) {
                grid.set(x, y, AStarNode(x, y))
            }
        }


        grid.get(0, 5)!!.wall = true
        grid.get(1, 5)!!.wall = true
        grid.get(2, 5)!!.wall = true
        grid.get(3, 5)!!.wall = true

        val aStar = AStar(grid, false)
        aStar.start = grid.get(0, 0)!!
        aStar.end = grid.get(1, 6)!!

        val start = System.nanoTime()
        var operations = 0
        aStar.start()
        while (!aStar.complete) {
            aStar.loop()
            operations++
        }
        aStar.path.forEach {
            println("${it.x}, ${it.y}")
        }
        println("A* took ${System.nanoTime() - start}ns and $operations ops")
    }
}