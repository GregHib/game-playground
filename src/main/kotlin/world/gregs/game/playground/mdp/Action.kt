package world.gregs.game.playground.mdp

import world.gregs.game.playground.Node

enum class Action {

    LEFT,
    RIGHT,
    UP,
    DOWN;

    fun opposite() = when(this) {
        LEFT -> RIGHT
        RIGHT -> LEFT
        UP -> DOWN
        DOWN -> UP
    }

    fun left() = when(this) {
        LEFT -> DOWN
        RIGHT -> UP
        UP -> LEFT
        DOWN -> RIGHT
    }

    fun right() = when(this) {
        LEFT -> UP
        RIGHT -> DOWN
        UP -> RIGHT
        DOWN -> LEFT
    }

    fun getSuccessor(node: Node) = when(this){
        LEFT -> Node(node.x-1, node.y)
        RIGHT -> Node(node.x+1, node.y)
        UP -> Node(node.x, node.y+1)
        DOWN -> Node(node.x, node.y-1)
    }
}