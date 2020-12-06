package world.gregs.game.playground.ai.iaus.bot.behaviour

import world.gregs.game.playground.ai.iaus.PlayerAIView.Companion.debug
import world.gregs.game.playground.ai.iaus.world.action.Action
import world.gregs.game.playground.ai.iaus.bot.Choice
import world.gregs.game.playground.ai.iaus.bot.Consideration
import world.gregs.game.playground.ai.iaus.world.Agent
import world.gregs.game.playground.ai.iaus.world.Named

interface Behaviour : Named {
    val targets: (Agent) -> List<Named>
    val considerations: Set<Consideration>
    val momentum: Double
    val weight: Double
    val action: Action

    /**
     * Combine [weight] with all considerations into one score
     * @return score 0..1 + [momentum]
     */
    fun score(agent: Agent, target: Any, last: Behaviour?): Double {
        val compensationFactor = 1.0 - (1.0 / considerations.size)
        var result = weight
        for (consideration in considerations) {
            var finalScore = consideration(agent, target)
            val modification = (1.0 - finalScore) * compensationFactor
            finalScore += (modification * finalScore)
            result *= finalScore
            if (result == 0.0) {
                return result
            }
        }

        if (this == last) {
            result *= momentum
        }
        return result
    }

    /**
     * Selects the target with the highest score greater than [highestScore]
     */
    fun getHighestTarget(agent: Agent, highestScore: Double, last: Behaviour?): Choice? {
        var highest = highestScore
        var topChoice: Named? = null
        val targets = targets(agent)
        for (target in targets) {
            if (highest > weight) {
                return null
            }

            val score = score(agent, target, last)
            if (debug) {
                println("Check target ${target.name} $score")
            }
            if (score > highest) {
                highest = score
                topChoice = target
            }
        }
        return if (topChoice != null) Choice(topChoice, this, highest) else null
    }

    companion object {
        val self: (Agent) -> List<Named> = { agent: Agent -> listOf(agent) }
    }
}