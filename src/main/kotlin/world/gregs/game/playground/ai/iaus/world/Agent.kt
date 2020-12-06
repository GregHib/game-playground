package world.gregs.game.playground.ai.iaus.world

import javafx.scene.paint.Color
import kotlinx.coroutines.*
import world.gregs.game.playground.ai.iaus.PlayerAIView.Companion.speed
import world.gregs.game.playground.ai.iaus.world.action.Action
import world.gregs.game.playground.ai.iaus.world.action.Actions
import world.gregs.game.playground.ai.iaus.bot.Reasoner
import world.gregs.game.playground.ai.iaus.bot.record.Records
import world.gregs.game.playground.spatial.sight.Distance.getNearest

data class Agent(
    override val name: String,
    override var x: Int,
    override var y: Int,
    override var colour: Color = Color.BLUE,
) : Actor {

    override lateinit var actions: Actions
    override lateinit var area: Area
    override val records = Records()

    enum class State {
        Idle,
        MoveTo,
        PerformAction
    }

    var actorState = State.Idle
    var job: Job? = null
    val reasoner = Reasoner(this)

    fun teleTo(targetX: Int, targetY: Int) = act(State.MoveTo) {
        x = targetX
        y = targetY
    }

    private suspend fun moveTo(targetX: Int, targetY: Int) {
        val previousState = actorState
        actorState = State.MoveTo
        while (x != targetX || y != targetY) {
            if (x != targetX) {
                x += (targetX - x).coerceIn(-1, 1)
            }
            if (y != targetY) {
                y += (targetY - y).coerceIn(-1, 1)
            }
            delay(speed)
        }
        actorState = previousState
    }

    fun act(action: Action, target: Named?, time: Long) {
        val handler = actions.getNull(action) ?: return
        val agent = this@Agent
        if (target is Coordinates) {
            act(State.PerformAction) {
                println("${agent.name} chose to $action ${target.name} [${time / 1000000.0}ms]")
                if(target is Area) {
                    agent.moveTo(getNearest(target.x, target.width, x), getNearest(target.y, target.height, y))
                } else {
                    agent.moveTo(target.x, target.y - 1)
                }
                when (action) {
                    Action.MoveToArea -> handler.handle(agent, target as Area)
                    Action.Pickup, Action.DepositLogs, Action.Chop -> handler.handle(agent, target as GameObject)
                    else -> println("Unknown target action $action")
                }
                agent.reasoner.behaviours.update(null)
            }
        } else {
            when(action) {
                Action.Idle -> {}
                else -> println("Unknown action $action")
            }
        }
    }

    private fun act(state: State, block: suspend () -> Unit) {
        if (this.actorState != State.Idle) {
            return
        }
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.Main) {
            this@Agent.actorState = state
            block.invoke()
            this@Agent.actorState = State.Idle
        }
    }

}