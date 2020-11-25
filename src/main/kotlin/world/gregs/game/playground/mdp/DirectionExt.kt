package world.gregs.game.playground.mdp

import world.gregs.game.playground.Direction
import world.gregs.game.playground.Node

fun Direction.getSuccessor(node: Node) = Node(node.x+x, node.y+y)

fun Direction.left() = when(this) {
    Direction.WEST -> Direction.SOUTH
    Direction.EAST -> Direction.NORTH
    Direction.NORTH -> Direction.WEST
    Direction.SOUTH -> Direction.EAST
    else -> throw Exception("Only supported for cardinal directions!")
}

fun Direction.right() = when(this) {
    Direction.WEST -> Direction.NORTH
    Direction.EAST -> Direction.SOUTH
    Direction.NORTH -> Direction.EAST
    Direction.SOUTH -> Direction.WEST
    else -> throw Exception("Only supported for cardinal directions!")
}