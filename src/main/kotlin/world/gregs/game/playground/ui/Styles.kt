package world.gregs.game.playground.ui

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.c
import tornadofx.cssclass
import tornadofx.em

class Styles : Stylesheet() {
    companion object {
        val primary = c("#404752")
        val secondary = c("#5c616d")
        val backgroundColour = c("#323337")
        val disabled = c("#7c808b")
        val darker = c("#35383c")
        val highlight = c("#7a90d8")
        val tileColour = Color.WHITE

        val test by cssclass()
        val tileLabel by cssclass()
        val background by cssclass()
    }

    init {
        label {
            fontSize = 1.5.em
            textFill = Color.WHITE
        }
        tileLabel {
            fontSize = 1.5.em
            textFill = Color.BLACK
        }
        background {
            backgroundColor += backgroundColour
        }
    }
}