package world.gregs.game.playground.ai.iaus.bot.behaviour

import world.gregs.game.playground.ai.iaus.PlayerAIView.Companion.debug
import world.gregs.game.playground.ai.iaus.bot.Choice
import world.gregs.game.playground.ai.iaus.world.Named

interface Behaviour<T> : Named {
    val targets: (T) -> List<Named>
    val considerations: Set<(T, Any) -> Double>
    val momentum: Double
    val weight: Double

    /**
     * Combine [weight] with all considerations into one score
     * @return score 0..1 + [momentum]
     */
    fun score(agent: T, target: Any, last: Behaviour<T>?): Double {
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
    fun getHighestTarget(agent: T, highestScore: Double, last: Behaviour<T>?): Choice<T>? {
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
}