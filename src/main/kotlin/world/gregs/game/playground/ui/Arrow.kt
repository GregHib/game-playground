package world.gregs.game.playground.ui

import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.*

import tornadofx.attachTo
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class Arrow(
    x1: Double,
    y1: Double,
    x2: Double,
    y2: Double,
    length: Double = defaultArrowHeadSize,
    offset: Double = 15.0
) : Path() {
    companion object {
        private const val defaultArrowHeadSize = 8.0
    }

    init {
        val dist = sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
        var t = offset / dist
        val startX = (((1 - t) * x1 + (t * x2)))
        val startY = ((1 - t) * y1 + (t * y2))
        t = (offset + length) / dist
        val endX = (((1 - t) * x1 + (t * x2)))
        val endY = ((1 - t) * y1 + (t * y2))

        val deltaX = (startY - endY) / 3
        val deltaY = (endX - startX) / 3

        strokeProperty().bind(fillProperty())
        fill = Color.BLACK

        //Line
        elements.add(MoveTo(x1, y1))
        elements.add(LineTo(x2, y2))
        elements.add(MoveTo(endX, endY))
        elements.add(LineTo(startX - deltaX, startY - deltaY))
        elements.add(LineTo(startX + deltaX, startY + deltaY))
        elements.add(LineTo(endX, endY))
    }
}

fun Parent.arrow(
    startX: Number = 0.0,
    startY: Number = 0.0,
    endX: Number = 0.0,
    endY: Number = 0.0,
    op: Arrow.() -> Unit = {}
) =
    Arrow(startX.toDouble(), startY.toDouble(), endX.toDouble(), endY.toDouble()).attachTo(this, op)