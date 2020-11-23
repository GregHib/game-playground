package world.gregs.game.playground.spatial.kdtree.kd

class Node<T : Dimensional>(
    var parentNode: Node<*>?,
    var leftNode: Node<*>?,
    var rightNode: Node<*>?,
    val data: T,
    val depth: Int
) :
    Dimensional {

    constructor(data: T) : this(null, null, null, data, 0)

    val isLeaf: Boolean
        get() = leftNode == null && rightNode == null

    override val coords: DoubleArray
        get() = data.coords

    override fun equals(other: Any?): Boolean {
        if (other is Node<*>) {
            val c1 = other.coords
            val c2 = coords
            return c1.contentEquals(c2)
        }
        return false
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

}