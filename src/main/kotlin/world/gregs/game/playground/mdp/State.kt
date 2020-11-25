package world.gregs.game.playground.mdp

import world.gregs.game.playground.Direction
import world.gregs.game.playground.Node

class State(private val x: Int, private val y: Int) {

    var utility = 0.0
    var reward = 0.0
    /**
     * Map of actions to probability-state pairs.
     */
    val transitions = HashMap<Direction, List<ProbableState>>()
    val actions = Direction.cardinal
    var policy: Direction? = null
    var coords = Node(x, y)
    var isGoal = false
    var isWall = false
    var id = 0

    fun computeExpectedUtility(action: Direction): Double {
        return transitions[action]!!.sumByDouble {
            it.probability * it.state.utility
        }
    }

    fun selectBestAction(): Direction {
        return actions.maxByOrNull {
            computeExpectedUtility(it)
        }!!
    }
}