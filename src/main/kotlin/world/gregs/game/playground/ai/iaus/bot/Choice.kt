package world.gregs.game.playground.ai.iaus.bot

import world.gregs.game.playground.ai.iaus.bot.behaviour.Behaviour
import world.gregs.game.playground.ai.iaus.world.Named

data class Choice<Agent, Target>(val target: Target, val behaviour: Behaviour<Agent, Target>, val score: Double)