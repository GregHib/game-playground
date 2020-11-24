package world.gregs.game.playground.ai.iaus.world.action

import world.gregs.game.playground.ai.iaus.world.Agent
import world.gregs.game.playground.ai.iaus.world.Area
import world.gregs.game.playground.ai.iaus.world.GameObject

interface ActionsHandler {

    open suspend fun handle(agent: Agent, target: GameObject) {

    }

    open suspend fun handle(agent: Agent, area: Area) {

    }

}
