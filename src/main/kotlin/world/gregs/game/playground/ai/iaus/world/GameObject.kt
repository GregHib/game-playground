package world.gregs.game.playground.ai.iaus.world

import javafx.scene.paint.Color
import world.gregs.game.playground.ai.iaus.world.action.Actions
import world.gregs.game.playground.ai.iaus.bot.record.Records

data class GameObject(
    override val name: String,
    override var x: Int,
    override var y: Int,
    override var colour: Color,
    override val records: Records = Records()
) : Actor {

    override lateinit var actions: Actions
    override lateinit var area: Area

}