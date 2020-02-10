package world.gregs.game.playground.pathfinding.jps.node

import javafx.scene.paint.Color
import java.util.*

enum class NodeStatus {
    OPEN, CLOSED, INACTIVE, JUMPED, PEEKED, VISITED;


    companion object {

        private val enumToColor = HashMap<NodeStatus, Color>()

        init {
            enumToColor[INACTIVE] = Color.WHITESMOKE
            enumToColor[OPEN] = Color.LIGHTBLUE
            enumToColor[CLOSED] = Color.DARKBLUE
            enumToColor[JUMPED] = Color.MEDIUMPURPLE
            enumToColor[PEEKED] = Color.HOTPINK
            enumToColor[VISITED] = Color.SALMON
        }

        fun color(status: NodeStatus): Color {
            return enumToColor[status]!!
        }
    }
}
