package world.gregs.game.playground.pathfinding.jps


import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import world.gregs.game.playground.pathfinding.jps.node.Option
import java.util.*

abstract class AbstractSearchSpace(val width: Int, val height: Int, protected var options: EnumSet<Option>) {

    fun allows(option: Option): Boolean {
        return options.contains(option)
    }

    fun allow(options: EnumSet<Option>) {
        this.options = options
    }

    protected fun resample(input: Image, newSize: Int): Image {
        val W = input.width.toInt()
        val H = input.height.toInt()
        val S = newSize / W

        println("Resampling " + W + " to " + S * H)

        if (S <= 1)
            return input

        val output = WritableImage(
            W * S,
            H * S
        )

        val reader = input.pixelReader
        val writer = output.pixelWriter

        for (y in 0 until H) {
            for (x in 0 until W) {
                val argb = reader.getArgb(x, y)
                for (dy in 0 until S) {
                    for (dx in 0 until S) {
                        writer.setArgb(x * S + dx, y * S + dy, argb)
                    }
                }
            }
        }

        return output
    }

}

