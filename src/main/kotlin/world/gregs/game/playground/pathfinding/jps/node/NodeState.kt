package world.gregs.game.playground.pathfinding.jps.node

import javafx.scene.paint.Color
import java.util.*

enum class NodeState private constructor(var value: Int) {
    EMPTY(0), WALL(1), START(10), GOAL(20);


    companion object {

        private val intToEnum = HashMap<Int, NodeState>()
        private val enumToColor = HashMap<NodeState, Color>()

        init {
            for (e in NodeState.values()) {
                intToEnum[e.value] = e
            }
        }

        init {
            enumToColor[EMPTY] = Color.WHITESMOKE
            enumToColor[WALL] = Color.rgb(20, 20, 20)
        }

        fun parse(value: Int): NodeState {
            return if (intToEnum.containsKey(value)) intToEnum[value]!! else EMPTY
        }

        fun color(state: NodeState): Color {
            return enumToColor[state]!!
        }
    }
}
