package world.gregs.game.playground.pathfinding.jps


import world.gregs.game.playground.Node
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import java.util.*

abstract class AbstractPathfinder {

    private var startTime: Long = 0
    protected var operations: Long = 0

    protected fun startClock() {
        startTime = System.nanoTime()
    }

    protected fun stopClock(): Long {
        return System.nanoTime() - startTime
    }

    protected fun reconstructPath(goal: JPSNode2): List<Node> {
        val reconstructedPath = ArrayList<Node>()

        var current: JPSNode2? = goal
        while (current != null) {
            reconstructedPath.add(current)
            current = current.parent
        }

        return reconstructedPath
    }


}
