package world.gregs.game.playground.ai.iaus.world

import javafx.scene.paint.Color
import world.gregs.game.playground.ai.iaus.bot.record.Recorder
import world.gregs.game.playground.ai.iaus.world.action.Actions

interface Actor : Named, Coordinates, Recorder {
    override var x: Int
    override var y: Int
    var colour: Color
    var actions: Actions
    var area: Area
}