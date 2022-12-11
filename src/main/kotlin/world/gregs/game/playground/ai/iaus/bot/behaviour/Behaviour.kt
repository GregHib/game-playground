package world.gregs.game.playground.ai.iaus.bot.behaviour

import world.gregs.game.playground.ai.iaus.PlayerAIView.Companion.debug
import world.gregs.game.playground.ai.iaus.bot.Choice
import world.gregs.game.playground.ai.iaus.world.Named

interface Behaviour<A, T> : Named {
    val targets: (A) -> List<T>
    val considerations: Set<(A, T) -> Double>
    val momentum: Double
    val weight: Double

    /**
     * Combine [weight] with all considerations into one score
     * @return score 0..1 + [momentum]
     */
    fun score(agent: A, target: T, last: Behaviour<A, T>?): Double {
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
    fun getHighestTarget(agent: A, highestScore: Double, last: Behaviour<A, T>?): Choice<A, T>? {
        var highest = highestScore
        var topChoice: T? = null
        val targets = targets(agent)
        for (target in targets) {
            if (highest > weight) {
                return null
            }

            if (debug) {
                println("Check target $target")
            }
            val score = score(agent, target, last)
            if (debug) {
                println("Target scored $target $score")
            }
            if (score > highest) {
                highest = score
                topChoice = target
            }
        }
        return if (topChoice != null) Choice(topChoice, this, highest) else null
    }

    /**
     * Scores all targets
     */
    fun getAllTargets(agent: A, last: Behaviour<A, T>?): List<Choice<A, T>?> {
        return targets(agent).map { target ->
            val score = score(agent, target, last)
            Choice(target, this, score)
        }
    }
}