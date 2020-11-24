package world.gregs.game.playground.ai.iaus.bot

import world.gregs.game.playground.ai.iaus.bot.behaviour.Behaviour
import world.gregs.game.playground.ai.iaus.world.Named

data class Choice(val target: Named, val behaviour: Behaviour, val score: Double)