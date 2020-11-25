package world.gregs.game.playground.mdp.policyiteration

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.SingularValueDecomposition
import world.gregs.game.playground.Grid
import world.gregs.game.playground.mdp.Action
import world.gregs.game.playground.mdp.State
import world.gregs.game.playground.mdp.probability
import world.gregs.game.playground.mdp.state

class PolicyIteration(private val grid: Grid<State>) {

    var complete = false
    // learning rate
    var gamma = 0.8
    var i = 0

    fun start(){
        i = 0
        complete = false
        for(x in grid.colIndices){
            for(y in grid.rowIndices){
                val state = grid.get(x, y)!!
                if(state.isGoal)
                    continue
                state.policy = state.actions.random()
            }
        }
    }

    fun loop(){
        complete = true
        calculateUtilitiesLinear()

        for(x in grid.colIndices){
            for(y in grid.rowIndices){
                val state = grid.get(x, y)!!
                if(state.isGoal)
                    continue
                var maxUtility = Double.NEGATIVE_INFINITY
                var bestAction: Action? = null
                for(action in state.actions){
                    val successor = grid.get(action.getSuccessor(state.coords)) ?: continue
                    val expectedUtility = successor.utility
                    if(expectedUtility > maxUtility) {
                        maxUtility = expectedUtility
                        bestAction = action
                    }
                }
                if(state.policy != bestAction) {
                    state.policy = bestAction
                    complete = false
                }
            }
        }
        i++
    }

    fun calculateUtilitiesLinear(){

        val nStates = grid.columns * grid.rows
        val coefficients = Array(nStates) {
            DoubleArray(nStates)
        }
        val ordinate = DoubleArray(nStates)

        for(x in grid.colIndices) {
            for (y in grid.rowIndices) {
                val state = grid.get(x, y)!!
                val row = state.id
                ordinate[row] = state.reward
                coefficients[row][row] += 1.0
                if(!state.isGoal){
                    val probableStates = state.transitions[state.policy]!!
                    for(pState in probableStates){
                        val col = pState.state.id
                        coefficients[row][col] += -gamma * pState.probability
                    }
                }
            }
        }

        val coefficientsMatrix = MatrixUtils.createRealMatrix(coefficients)
        val svd = SingularValueDecomposition(coefficientsMatrix)
        val ordinateVector = MatrixUtils.createRealVector(ordinate)
        val solutionVector = svd.solver.solve(ordinateVector)

        for(x in grid.colIndices) {
            for (y in grid.rowIndices) {
                val state = grid.get(x, y)!!
                state.utility = solutionVector.getEntry(state.id)
            }
        }
    }
}