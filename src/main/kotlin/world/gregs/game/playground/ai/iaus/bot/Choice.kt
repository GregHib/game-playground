package world.gregs.game.playground.ai.iaus.bot

import world.gregs.game.playground.ai.iaus.bot.behaviour.Behaviour
import world.gregs.game.playground.ai.iaus.world.Named

data class Choice<T>(val target: Named, val behaviour: Behaviour<T>, val score: Double)