package world.gregs.game.playground.ui

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Color
import tornadofx.Controller
import kotlin.math.floor

class GridController : Controller() {

    var hoverX = SimpleIntegerProperty(0)
    var hoverY = SimpleIntegerProperty(0)

    var tileBorder = SimpleDoubleProperty(2.0)
    var tileSize = SimpleDoubleProperty(40.0)
    var rows = SimpleIntegerProperty(25) // max 300
    var columns = SimpleIntegerProperty(25)

    var coordinates = SimpleStringProperty("X: 0 Y: 0")


    val entities = mutableListOf(Entity(Color.YELLOW, "1.0"))

    fun updateSize(height: Double) {
        val approxSize = floor(height) / rows.get()
        val approxBorder = approxSize * 0.04
        val size = floor(height - approxBorder) / rows.get()
        tileBorder.set(approxBorder)
        tileSize.set(size - approxBorder)
    }
}