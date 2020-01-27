package world.gregs.game.playground.ui

import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color

data class Entity(val colour: Color, val text: String? = null) {
    val xProperty = SimpleIntegerProperty(0)
    var x: Int
        get() = xProperty.get()
        set(value) = xProperty.set(value)
    val yProperty = SimpleIntegerProperty(0)
    var y: Int
        get() = yProperty.get()
        set(value) = yProperty.set(value)
}