package world.gregs.game.playground.ui.zoom

import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventTarget
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import tornadofx.FX.Companion.primaryStage
import tornadofx.add
import tornadofx.addClass
import tornadofx.opcr
import world.gregs.game.playground.ui.Styles


/**
 * A canvas which can be scaled
 */
open class ZoomCanvas(paddingX: Double, paddingY: Double, minZoom: Double, maxZoom: Double) : Pane() {

    private var canvasScale = SimpleDoubleProperty(1.0)

    val content = Pane()

    /**
     * Set x/y scale
     */
    var scale: Double
        get() = canvasScale.get()
        set(scale) = canvasScale.set(scale)

    init {
        // Add scale transform
        content.scaleXProperty().bind(canvasScale)
        content.scaleYProperty().bind(canvasScale)
        // Clip canvas to view
        val wrapper = Rectangle()
        wrapper.widthProperty().bind(widthProperty())
        wrapper.heightProperty().bind(heightProperty())
        clip = wrapper

        addClass(Styles.background)

        // Border around canvas
        prefWidth = content.prefWidth + paddingX
        prefHeight = content.prefHeight + paddingY

        // Set start in centre
        content.translateX = paddingX / 2
        content.translateY = paddingY / 2

        add(content)

        val gestures = SceneGestures(this, minZoom, maxZoom)

        content.addEventFilter(MouseEvent.MOUSE_MOVED, gestures.onMouseMovedEventHandler)
        content.addEventFilter(MouseEvent.MOUSE_DRAGGED, gestures.onMouseMovedEventHandler)
        content.addEventFilter(MouseEvent.MOUSE_PRESSED, gestures.onMousePressedEventHandler)
        content.addEventFilter(MouseEvent.MOUSE_DRAGGED, gestures.onMouseDraggedEventHandler)
        addEventFilter(ScrollEvent.ANY, gestures.onScrollEventHandler)

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, gestures.onKeyPressedEventHandler)
        primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, gestures.onKeyReleasedEventHandler)
    }

    /**
     * Set x/y pivot points
     * @param x
     * @param y
     */
    fun setPivot(x: Double, y: Double) {
        content.translateX -= x
        content.translateY -= y
    }
}

fun EventTarget.zoom(
    paddingX: Double,
    paddingY: Double,
    minZoom: Double,
    maxZoom: Double,
    op: ZoomCanvas.() -> Unit = {}
) = opcr(this, ZoomCanvas(paddingX, paddingY, minZoom, maxZoom), op)