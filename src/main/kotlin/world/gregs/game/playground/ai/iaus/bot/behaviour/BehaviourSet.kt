package world.gregs.game.playground.ai.iaus.bot.behaviour

import world.gregs.game.playground.ai.iaus.PlayerAIView.Companion.debug
import world.gregs.game.playground.ai.iaus.bot.Choice
import world.gregs.game.playground.ai.iaus.world.Agent

class BehaviourSet<T>(
    private val set: MutableSet<Behaviour<T>> = mutableSetOf()
) : MutableSet<Behaviour<T>> by set {

    var current: Choice<T>? = null
    var last: Choice<T>? = null

    fun update(choice: Choice<T>?) {
        last = current
        current = choice
    }

    fun select(agent: T): Choice<T>? {
        if (debug) {
            println("Selecting behaviour from ${set.map { it.name }}")
        }
        val choice = set.fold(null as Choice<T>?) { highest, behaviour ->
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