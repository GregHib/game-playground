package world.gregs.game.playground.mdp

import world.gregs.game.playground.Grid
import world.gregs.game.playground.Node
import world.gregs.game.playground.ui.zoom.SolidGrid

class Maze {

    companion object {
        const val COLUMNS = 10
        const val ROWS = 10
    }

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
                        0.1 to filter(state, a.inverse().getSuccessor(state.coords)),
                        0.1 to filter(state, a.left().getSuccessor(state.coords)),
                        0.1 to filter(state, a.right().getSuccessor(state.coords))
                    )
                }
            }
        }
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
}

fun Grid<State>.get(node: Node): State?{
    return get(node.x, node.y)
}
