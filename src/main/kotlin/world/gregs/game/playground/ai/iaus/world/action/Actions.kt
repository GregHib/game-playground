package world.gregs.game.playground.ai.iaus.world.action

class Actions {
    private val handlers = mutableMapOf<Action, ActionsHandler>()

    fun set(behaviour: Action, handler: ActionsHandler) {
        handlers[behaviour] = handler
    }

    fun getHandler(behaviour: Action) = handlers.getValue(behaviour)

    fun getNull(behaviour: Action) = handlers[behaviour]

    fun getActions() = handlers.keys
}