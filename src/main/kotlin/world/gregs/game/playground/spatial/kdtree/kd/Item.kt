package world.gregs.game.playground.spatial.kdtree.kd

internal class Item(x: Int, y: Int) : Dimensional {
    override val coords: DoubleArray = doubleArrayOf(x.toDouble(), y.toDouble())
}