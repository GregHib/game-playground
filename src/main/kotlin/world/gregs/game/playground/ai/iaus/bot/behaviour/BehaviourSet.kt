package world.gregs.game.playground.ai.iaus.bot.behaviour

import world.gregs.game.playground.ai.iaus.PlayerAIView.Companion.debug
import world.gregs.game.playground.ai.iaus.bot.Choice
import world.gregs.game.playground.ai.iaus.world.Agent

class BehaviourSet<A, T>(
    private val set: MutableSet<Behaviour<A, T>> = mutableSetOf()
) : MutableSet<Behaviour<A, T>> by set {

    var current: Choice<A, T>? = null
    var last: Choice<A, T>? = null

    fun update(choice: Choice<A, T>?) {
        last = current
        current = choice
    }

    fun select(agent: A): Choice<A, T>? {
        if (debug) {
            println("Selecting behaviour from ${set.map { it.name }}")
        }
        val choice = set.fold(null as Choice<A, T>?) { highest, behaviour ->
            val target = behaviour.getHighestTarget(agent, highest?.score ?: 0.0, last?.behaviour)
            if (debug) {
                println("Highest target for ${behaviour.name} ${target?.target ?: "none"} ${target?.score ?: 0.0}")
            }
            target ?: highest
        }
        update(choice)
        return current
    }

    fun debug(agent: A): List<Choice<A, T>> {
        return set.flatMap { behaviour -> behaviour.getAllTargets(agent, null) }.filterNotNull()
    }
}