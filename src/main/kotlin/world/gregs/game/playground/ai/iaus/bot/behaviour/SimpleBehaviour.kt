package world.gregs.game.playground.ai.iaus.bot.behaviour

import world.gregs.game.playground.ai.iaus.world.action.Action
import world.gregs.game.playground.ai.iaus.bot.Consideration
import world.gregs.game.playground.ai.iaus.world.Agent
import world.gregs.game.playground.ai.iaus.world.Named
import world.gregs.game.playground.ai.iaus.bot.behaviour.Behaviour.Companion.self

data class SimpleBehaviour(
    override val name: String,
    override val considerations: Set<Consideration> = setOf(),
    override val action: Action,
    override val targets: (Agent) -> List<Named> = self,
    override val momentum: Double = 1.25,
    override val weight: Double = 1.0
) : Behaviour