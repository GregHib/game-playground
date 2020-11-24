package world.gregs.game.playground.ai.iaus.world.action

import world.gregs.game.playground.ai.iaus.world.*
import kotlin.reflect.KClass

class ActionsProvider {

    private val defaultHandlers = mutableMapOf<KClass<out Actor>, MutableMap<Action, ActionsHandler>>()

    inline fun <reified T : Actor> set(action: Action, handler: ActionsHandler) {
        set(T::class, action, handler)
    }

    fun set(clazz: KClass<out Actor>, action: Action, handler: ActionsHandler) {
        defaultHandlers.getOrPut(clazz) { mutableMapOf() }[action] = handler
    }

    fun produce(actor: Actor) {
        val actions = Actions()
        defaultHandlers[actor::class]?.forEach { (action, handler) ->
            actions.set(action, handler)
        }
        actor.actions = actions
    }

}

inline fun <reified T : Actor> ActionsProvider.obj(
    action: Action,
    noinline block: suspend (Agent, GameObject) -> Unit
) = set(T::class, action, object : ActionsHandler {
    override suspend fun handle(agent: Agent, target: GameObject) {
        block.invoke(agent, target)
    }
})

inline fun <reified T : Actor> ActionsProvider.area(
    action: Action,
    noinline block: suspend (Agent, Area) -> Unit
) = set(T::class, action, object : ActionsHandler {
    override suspend fun handle(agent: Agent, area: Area) {
        block.invoke(agent, area)
    }
})