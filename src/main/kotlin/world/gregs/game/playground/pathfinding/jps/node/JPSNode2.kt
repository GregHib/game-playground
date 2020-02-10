package world.gregs.game.playground.pathfinding.jps.node

import world.gregs.game.playground.Node

class JPSNode2(x: Int, y: Int) : Node(x, y), Comparable<JPSNode2> {

    var parent: JPSNode2? = null
    var g: Double = 0.toDouble()
    var h: Double = 0.toDouble()
    var state: NodeState
    var status: NodeStatus

    val f: Double
        get() = g + h

    init {
        state = NodeState.EMPTY
        status = NodeStatus.INACTIVE
    }

    constructor(x: Int, y: Int, state: NodeState) : this(x, y) {
        this.state = state
    }

    override fun compareTo(node: JPSNode2): Int {
        val f1 = f
        val f2 = node.f
        if (areEqualDouble(f1, f2, 6)) {
            if (areEqualDouble(this.g, node.g, 6))
                return 0
            else if (this.g > node.g)
                return -1
            else if (this.g < node.g)
                return 1
        } else if (f1 < f2)
            return -1
        else if (f1 > f2)
            return 1

        return 0
    }

    fun copy(): JPSNode2 {
        val copy = JPSNode2(x, y)
        copy.parent = parent
        copy.g = g
        copy.h = h
        copy.state = state
        copy.status = status
        return copy
    }

    override fun toString(): String {
        return String.format(
            "%s G=%.2f H=%.2f F=%.2f STATE=%s STATUS=%s PARENT={x=%d,y=%d}",
            super.toString(),
            g,
            h,
            f,
            state,
            status,
            if (parent != null) parent!!.x else -1,
            if (parent != null) parent!!.y else -1
        )
    }

    companion object {

        fun areEqualDouble(a: Double, b: Double, precision: Int): Boolean {
            return Math.abs(a - b) <= Math.pow(10.0, (-precision).toDouble())
        }
    }

}
