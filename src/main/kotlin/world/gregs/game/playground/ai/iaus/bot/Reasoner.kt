package world.gregs.game.playground.ai.iaus.bot

import world.gregs.game.playground.ai.iaus.bot.behaviour.BehaviourSet
import world.gregs.game.playground.ai.iaus.bot.behaviour.SimpleBehaviour
import world.gregs.game.playground.ai.iaus.world.Agent
import world.gregs.game.playground.ai.iaus.world.Named
import kotlin.system.measureNanoTime

class Reasoner(private val agent: Agent) {
    val behaviours = BehaviourSet<Agent, Named>()

    fun tick() {
        val choice: Choice<Agent, Named>
        val time = measureNanoTime {
            choice = behaviours.select(agent)!!
        }
        val behaviour = choice.behaviour
        if (behaviour is SimpleBehaviour) {
            agent.act(behaviour.action, choice.target, time)
        }
    }

}