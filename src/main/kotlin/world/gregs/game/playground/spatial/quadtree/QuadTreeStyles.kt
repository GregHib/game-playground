package world.gregs.game.playground.spatial.quadtree

import tornadofx.Stylesheet
import tornadofx.c
import tornadofx.cssclass

class QuadTreeStyles : Stylesheet() {
    companion object {
        val backgroundColour = c("#323337")
        val background by cssclass()
    }

    init {
        background {
            backgroundColor += backgroundColour
        }
    }
}