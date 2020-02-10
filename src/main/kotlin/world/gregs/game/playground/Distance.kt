package world.gregs.game.playground

import kotlin.math.abs
import kotlin.math.sqrt


typealias Distance = (x1: Int, y1: Int, x2: Int, y2: Int) -> Double

val manhattan: Distance = { x1, y1, x2, y2 -> (abs(x1 - x2) + abs(y1 - y2)).toDouble() }//Diamond
val chebyshev: Distance = { x1, y1, x2, y2 -> abs(x1 - x2).coerceAtLeast(abs(y1 - y2)).toDouble() }//Square
val euclidean: Distance = { x1, y1, x2, y2 -> sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)).toDouble()) }//Circle
