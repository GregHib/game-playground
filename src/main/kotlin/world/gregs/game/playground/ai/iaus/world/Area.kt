package world.gregs.game.playground.ai.iaus.world

import javafx.scene.paint.Color
import world.gregs.game.playground.ai.iaus.bot.behaviour.Behaviour
import world.gregs.game.playground.ai.iaus.bot.record.Recorder
import world.gregs.game.playground.ai.iaus.world.action.Action
import world.gregs.game.playground.ai.iaus.bot.record.Records

data class Area(
    override val name: String,
    override val x: Int,
    override val y: Int,
    val width: Int,
    val height: Int,
    val colour: Color,
    override val records: Records = Records(),
    val behaviours: MutableSet<Behaviour> = mutableSetOf()
) : Coordinates, Named, Recorder {
    val actors = mutableListOf<Actor>()

    val objects: List<GameObject>
        get() = actors.filterIsInstance<GameObject>()

    val actions: Set<Action>
        get() = actors.flatMap { it.actions.getActions() }.toSet()

    fun add(actor: Actor) {
        actors.add(actor)
        actor.area = this
        if (actor is Agent) {
            actor.reasoner.behaviours.addAll(behaviours)
        }
    }

    fun remove(actor: Actor) {
        actors.remove(actor)
        if (actor is Agent) {
            actor.reasoner.behaviours.removeAll(behaviours)
        }
    }
}