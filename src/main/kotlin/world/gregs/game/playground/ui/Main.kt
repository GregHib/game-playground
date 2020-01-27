package world.gregs.game.playground.ui

import tornadofx.App
import tornadofx.launch

class TornadoApp : App(MainView::class, Styles::class) {
    init {
//        reloadViewsOnFocus()
//        reloadStylesheetsOnFocus()
    }
}

fun main(args: Array<String>) {
    println(System.getProperties().toList())
    launch<TornadoApp>(*args)
}