package world.gregs.game.playground.ui.zoom

import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import tornadofx.FX.Companion.primaryStage

/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
internal class SceneGestures(
    private var canvas: ZoomCanvas,
    private val minScale: Double,
    private val maxScale: Double
) {
    /**
     * Mouse drag context used for scene and nodes.
     */
    private inner class DragContext {

        var mouseAnchorX = 0.0
        var mouseAnchorY = 0.0

        var anchorX = 0.0
        var anchorY = 0.0

    }

    private var mouseX = 0.0
    private var mouseY = 0.0

    private val sceneDragContext = DragContext()
    private var spacePressed = false
    private val content = canvas.content

    init {
        setOnResize { _, _, _ ->
            clampTranslation()
        }
    }

    // Track mouse position
    val onMouseMovedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        mouseX = event.sceneX
        mouseY = event.sceneY
    }

    // Click to start panning if space is held
    val onMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!spacePressed) {
            return@EventHandler
        }
        begin()
        event.consume()
    }

    // Drag moves canvas if space is held
    val onMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!spacePressed) {
            return@EventHandler
        }

        content.translateX = sceneDragContext.anchorX + event.sceneX - sceneDragContext.mouseAnchorX
        content.translateY = sceneDragContext.anchorY + event.sceneY - sceneDragContext.mouseAnchorY
        clampTranslation()

        event.consume()
    }

    /**
     * Clamps the canvas translation by half of it's height and width
     * FIXME what if limits exceed window due to excessive zoom?
     */
    private fun clampTranslation() {
        /*val halfWidth = canvas.width / 2
        val halfHeight = canvas.height / 2
        content.translateX = content.translateX.coerceIn(-halfWidth, canvas.layoutBounds.width - halfWidth)
        content.translateY = content.translateY.coerceIn(-halfHeight, canvas.layoutBounds.height - halfHeight)*/
    }

    /**
     * Marks the current mouse and canvas position in [sceneDragContext]
     */
    private fun begin() {
        sceneDragContext.mouseAnchorX = mouseX
        sceneDragContext.mouseAnchorY = mouseY

        sceneDragContext.anchorX = content.translateX
        sceneDragContext.anchorY = content.translateY
    }

    /**
     * Mouse wheel handler: zoom to pivot point
     * Note: currently we only use Y, same value is used for X
     *       pivot value must be untransformed, i. e. without scaling
     */
    val onScrollEventHandler: EventHandler<ScrollEvent> = EventHandler { event ->
        val delta = 1.2

        var scale = canvas.scale
        val oldScale = scale

        if (event.deltaY < 0)
            scale /= delta
        else
            scale *= delta

        scale = scale.coerceIn(minScale, maxScale)

        val f = scale / oldScale - 1

        val dx = event.x - (content.boundsInParent.width / 2 + content.boundsInParent.minX)
        val dy = event.y - (content.boundsInParent.height / 2 + content.boundsInParent.minY)

        canvas.scale = scale
        canvas.setPivot(f * dx, f * dy)

        event.consume()
    }

    val onKeyPressedEventHandler: EventHandler<KeyEvent> = EventHandler { event ->
        if (event.code == KeyCode.SPACE && !spacePressed) {
            spacePressed = true
            begin()
            event.consume()
        }
    }

    val onKeyReleasedEventHandler: EventHandler<KeyEvent> = EventHandler { event ->
        if (event.code == KeyCode.SPACE && spacePressed) {
            spacePressed = false
            event.consume()
        }
    }
}

fun setOnResize(listener: (ObservableValue<out Number>, Number, Number) -> Unit) {
    primaryStage.widthProperty().addListener(listener)
    primaryStage.heightProperty().addListener(listener)
}