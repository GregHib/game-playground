package world.gregs.game.playground.ai.iaus.bot

inline fun <A, reified T : Any> consider(crossinline block: (A, T) -> Double):  (A, Any) -> Double = { agent, any ->
    if (any is T) block.invoke(agent, any) else 0.0
}

inline fun <A, reified T : Any> considerBool(crossinline block: (A, T) -> Boolean):  (A, Any) -> Double = { agent, any ->
    if (any is T) if (block.invoke(agent, any)) 1.0 else 0.0 else 0.0
}