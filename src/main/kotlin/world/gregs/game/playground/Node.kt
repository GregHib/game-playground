package world.gregs.game.playground

import kotlin.math.abs

open class Node(val x: Int, val y: Int) {

    fun delta(v: Node): Node {
        return Node(abs(this.x - v.x), abs(this.y - v.y))
    }

    override fun toString(): String {
        return String.format("{x=%d,y=%d}", x, y)
    }
}