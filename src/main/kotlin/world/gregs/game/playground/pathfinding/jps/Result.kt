package world.gregs.game.playground.pathfinding.jps

import world.gregs.game.playground.Node

class Result {
    private val path: List<Node>
    private val duration: Long
    private val operations: Long
    private val cost: Double

    constructor(path: List<Node>, duration: Long, operations: Long) {
        this.path = path
        this.duration = duration
        this.operations = operations
        this.cost = path.size.toDouble()
    }

    constructor(path: List<Node>, cost: Double, duration: Long, operations: Long) {
        this.path = path
        this.duration = duration
        this.operations = operations
        this.cost = cost
    }

    /**
     * @return A list consisting of the nodes used in the found path
     */
    fun path(): List<Node> {
        return path
    }

    /**
     * @return The duration of the search, in milliseconds
     */
    fun duration(): Long {
        return duration
    }

    /**
     * @return The number of operations performed during the search
     */
    fun operations(): Long {
        return operations
    }

    /**
     * @return The final cost of the found path
     */
    fun cost(): Double {
        return cost
    }
}
