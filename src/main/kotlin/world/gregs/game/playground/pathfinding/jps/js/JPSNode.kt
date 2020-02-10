package world.gregs.game.playground.pathfinding.jps.js

open class JPSNode(internal var x: Int, internal var y: Int) {
    internal var g: Float = 0.toFloat()
    internal var h: Float = 0.toFloat()
    internal var f: Float = 0.toFloat()  //g = from start; h = to end, f = both together
    internal var pass: Boolean = false
    var parent: JPSNode? = null
    var walkable = true

    init {
        this.pass = true
    }

    fun updateGHFP(g: Float, h: Float, parent: JPSNode?) {
        this.parent = parent
        this.g = g
        this.h = h
        f = g + h
    }

    fun setPass(pass: Boolean): Boolean {
        this.pass = pass
        return pass
    }
}