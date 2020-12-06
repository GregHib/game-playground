package world.gregs.game.playground.ai.iaus.bot.behaviour

import world.gregs.game.playground.ai.iaus.PlayerAIView.Companion.debug
import world.gregs.game.playground.ai.iaus.bot.Choice
import world.gregs.game.playground.ai.iaus.world.Agent

class BehaviourSet(
    private val set: MutableSet<Behaviour> = mutableSetOf()
) : MutableSet<Behaviour> by set {

    var current: Choice? = null
    var last: Choice? = null

    fun update(choice: Choice?) {
        last = current
        current = choice
    }

    fun select(agent: Agent): Choice? {
        if (debug) {
            println("Selecting behaviour from ${set.map { it.name }}")
        }
        val choice = set.fold(null as Choice?) { highest, behaviour ->
            val target = behaviour.getHighestTarget(agent, highest?.score ?: 0.0, last?.behaviour)
            if (debug) {
                println("Highest target for ${behaviour.name} ${target?.target?.name ?: "none"} ${target?.score ?: 0.0}")
            }
            target ?: highest
        }
        update(choice)
        return current
    }

    companion object {
    }
}