package world.gregs.game.playground.mdp.valueiteration

import world.gregs.game.playground.Grid
import world.gregs.game.playground.mdp.State
import kotlin.math.abs

class ValueIteration(private val grid: Grid<State>) {

    var complete = false
    // learning rate
    var gamma = 0.8
    var stopCirteria = 0.001
    var i = 0

    fun start(){
        i = 0
        complete = false
        for(x in grid.colIndices){
            for(y in grid.rowIndices){
                val state = grid.get(x, y)!!
                if(state.isGoal)
                    continue
                state.utility = 0.0
            }
        }
    }

    fun loop(){
        complete = true
        for(x in grid.colIndices){
            for(y in grid.rowIndices){
                val state = grid.get(x, y)!!
                if(state.isGoal)
                    continue
                var maxUtility = Double.NEGATIVE_INFINITY
                for(action in state.actions){
                    val expectedUtility = state.reward + (gamma * state.computeExpectedUtility(action))
                    if(expectedUtility > maxUtility)
                        maxUtility = expectedUtility
                }
                if(abs(maxUtility-state.utility) > stopCirteria)
                    complete = false
                state.utility = maxUtility
            }
        }
        i++
    }
}