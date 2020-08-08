package world.gregs.game.playground.spacial.kdtree.kd

internal class Item(x: Int, y: Int) : Dimensional {
    override val coords: DoubleArray = doubleArrayOf(x.toDouble(), y.toDouble())
}