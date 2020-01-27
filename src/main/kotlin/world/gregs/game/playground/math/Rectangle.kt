package world.gregs.game.playground.math

import kotlin.random.Random

data class Rectangle(val x: Int, val y: Int, val width: Int, val height: Int) {
    fun contains(point: Point): Boolean {
        return point.x in x..x + width && point.y in y..y + height
    }

    fun intersects(r: Rectangle): Boolean {
        return !(r.x > x + width && r.y > y + height || r.x + r.width < x && r.y + r.height < y)
    }

    fun randomPoint(): Point = Point(Random.nextInt(x, x + width), Random.nextInt(y, y + height))
}