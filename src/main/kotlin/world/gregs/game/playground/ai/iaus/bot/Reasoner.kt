package world.gregs.game.playground.ai.iaus.bot

import world.gregs.game.playground.ai.iaus.bot.behaviour.BehaviourSet
import world.gregs.game.playground.ai.iaus.world.Agent
import kotlin.system.measureNanoTime

class Reasoner(private val agent: Agent) {
    val behaviours = BehaviourSet<Agent>()

    fun tick() {
        val choice: Choice<Agent>
        val time = measureNanoTime {
            choice = behaviours.select(agent)!!
        }
        agent.act(choice.behaviour.action, choice.target, time)
    }

}