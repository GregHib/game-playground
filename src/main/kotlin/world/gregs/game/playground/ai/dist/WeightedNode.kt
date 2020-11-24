package world.gregs.game.playground.ai.dist

abstract class WeightedNode {
    abstract var weight: Long

    open var isParent: Boolean = false

    open fun sample(): WeightedNode? = null

    fun sampleOrNull(): WeightedNode? {
        var node = sample()
        while(node != null && node.isParent) {
            node = node.sampleOrNull()
        }
        return node
    }

}