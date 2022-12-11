package world.gregs.game.playground.ai.iaus.bot.behaviour

import world.gregs.game.playground.ai.iaus.world.action.Action
import world.gregs.game.playground.ai.iaus.world.Agent
import world.gregs.game.playground.ai.iaus.world.Named

data class SimpleBehaviour(
    override val name: String,
    override val considerations: Set<(Agent, Any) -> Double> = setOf(),
    override val action: Action,
    override val targets: (Agent) -> List<Named> = self,
    override val momentum: Double = 1.25,
    override val weight: Double = 1.0
) : Behaviour<Agent> {
    companion object {
        val self: (Agent) -> List<Named> = { agent: Agent -> listOf(agent) }
    }
}