package world.gregs.game.playground.ai.iaus.bot

import world.gregs.game.playground.ai.iaus.world.Agent

typealias Consideration = (Agent, Any) -> Double

inline fun <reified T : Any> consider(crossinline block: (Agent, T) -> Double): Consideration = { agent, any ->
    if (any is T) block.invoke(agent, any) else 0.0
}

inline fun <reified T : Any> considerBool(crossinline block: (Agent, T) -> Boolean): Consideration = { agent, any ->
    if (any is T) if (block.invoke(agent, any)) 1.0 else 0.0 else 0.0
}