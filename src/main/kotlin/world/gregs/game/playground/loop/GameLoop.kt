package world.gregs.game.playground.loop

import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*
import world.gregs.game.playground.spatial.quadtree.QuadTreeStyles
import world.gregs.game.playground.ui.zoom.zoom
import java.awt.Rectangle
import java.lang.Runnable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class GameLoopView : View("GameLoop"), Runnable {

    data class Point(val x: Int, val y: Int, var time: Int)

    private val dispatcher = newSingleThreadContext("Game loop")
    companion object {
        private val boundary = Rectangle(0, 0, 512, 512)
        const val PADDING = 100.0
    }

    val points = mutableListOf<Point>()
    lateinit var counter: Text
    lateinit var stage: Text
    val executor = Executors.newSingleThreadScheduledExecutor()
    var cycle = 0L

    fun start() {
        executor.scheduleAtFixedRate(this, 0, 10000, TimeUnit.MILLISECONDS)
    }

    override fun run() {
        try {
            process()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun process() {
        runBlocking(dispatcher) {
            counter.text = "Cycle ${cycle + 1}"
            stage.text = "Stage pre"
            pre()
            yield()

            stage.text = "Stage center"
            center()
            yield()

            stage.text = "Stage post"
            post()
            yield()

            stage.text = "Stage complete"
        }
        cycle++
    }


    val flow = MutableStateFlow(0L)

    suspend fun pre() {
        println("Pre")
        flow.emit(cycle)
        delay(2000)
    }

    suspend fun center() {
        println("Center")
        delay(2000)
    }

    suspend fun post() {
        println("Post")
        delay(2000)
    }

    override val root = zoom(PADDING, PADDING, 1.0, 10.0) {
        prefWidth = boundary.width + PADDING
        prefHeight = boundary.height + PADDING
        counter = text("Cycle: 0") {
            x = PADDING / 4
            y = PADDING / 4
            fill = Color.BLACK
        }
        stage = text("Stage: ") {
            x = PADDING / 4 + 50.0
            y = PADDING / 4
            fill = Color.BLACK
        }
        fun reload() {
            content.clear()
            content.rectangle(boundary.x, boundary.y, boundary.width, boundary.height) {
                stroke = Color.WHITE
                fill = null
            }
            points.forEach { point ->
                content.circle(point.x, point.y, 15) {
                    fill = Color.HOTPINK
                }
                content.text(point.time.toString()) {
                    textAlignment = TextAlignment.CENTER
                    x = point.x.toDouble() - layoutBounds.width / 2
                    y = point.y.toDouble() + layoutBounds.height / 3
                    fill = Color.WHITE
                }
            }
        }

        reload()

        content.setOnMouseClicked {
            val point = Point(it.x.toInt(), it.y.toInt(), 10)
            points.add(point)
            reload()

            GlobalScope.launch(dispatcher) {
                println("Launch")
                repeat(10) {
                    wait()

                    println("Dec")
                    point.time--

                    withContext(Dispatchers.JavaFx) {
                        reload()
                    }

//                    val start = cycle
//                    flow.filter { cycle -> cycle >= start + 2 }.singleOrNull()
                }
            }
        }
    }

    suspend fun wait() = flow.singleOrNull()

    suspend fun wait(cycles: Int) = repeat(cycles) { wait() }


    init {
        start()
    }

}

class GameLoopApp : App(GameLoopView::class, QuadTreeStyles::class)

fun main(args: Array<String>) {
    val defer = CompletableDeferred<String>()
    runBlocking {
    launch {
        println("Await completion")
        println(defer.await())
        println("Await again")
        println(defer.await())
    }
    defer.complete("One")
    defer.complete("Two")

    }
//    launch<GameLoopApp>(*args)
}