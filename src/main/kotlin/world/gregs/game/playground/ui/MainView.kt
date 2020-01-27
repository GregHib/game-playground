package world.gregs.game.playground.ui

import javafx.beans.binding.DoubleBinding
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.*

class MainView : View("App") {

    private val controller: GridController by inject()

    private fun updateMouse(event: MouseEvent, tile: DoubleBinding) {
        val halfBorder = controller.tileBorder.get() / 2
        val x = ((event.x + halfBorder) / tile.get()).toInt().coerceAtMost(controller.columns.get() - 1)
        val y = ((event.x + halfBorder) / tile.get()).toInt().coerceAtMost(controller.rows.get() - 1)
        controller.hoverX.set(x)
        controller.hoverY.set(y)
        controller.coordinates.set("X: $x Y: $y")
    }

    override val root = stackpane {
        addClass(Styles.background)
        vbox {
            alignment = Pos.CENTER
            hbox {
                alignment = Pos.CENTER
                pane {
                    val tile = controller.tileSize.plus(controller.tileBorder)
                    //Calculate reusable properties
                    val horizontals = arrayOfNulls<DoubleBinding>(controller.rows.get())
                    for (y in 0 until controller.rows.get()) {
                        horizontals[y] = tile.multiply(y)
                    }
                    //Add and bind tiles
                    for (x in 0 until controller.columns.get()) {
                        val xProperty = tile.multiply(x)
                        for (y in 0 until controller.rows.get()) {
                            rectangle {
                                //Center
                                xProperty().bind(xProperty)
                                yProperty().bind(horizontals[y])
                                widthProperty().bind(controller.tileSize)
                                heightProperty().bind(controller.tileSize)
                                fill = Styles.tileColour
                            }
                        }
                    }

                    setOnMouseMoved {
                        updateMouse(it, tile)
                    }
                    setOnMouseDragged {
                        updateMouse(it, tile)
                    }
                }
            }
        }

        heightProperty().addListener { _, _, height ->
            controller.updateSize(height.toDouble())
        }

        label {
            textProperty().bind(controller.coordinates)
            StackPane.setAlignment(this, Pos.BOTTOM_LEFT)
            effect = DropShadow(2.0, Color.BLACK)
        }
    }
}