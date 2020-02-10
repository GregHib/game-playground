package world.gregs.game.playground.pathfinding.jps


import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import world.gregs.game.playground.pathfinding.jps.node.JPSNode2
import world.gregs.game.playground.pathfinding.jps.node.NodeState
import world.gregs.game.playground.pathfinding.jps.node.Option
import java.security.SecureRandom
import java.util.*

class JPSGrid private constructor(width: Int, height: Int) :
    AbstractSearchSpace(width, height, EnumSet.of(Option.DIAGONAL_MOVEMENT, Option.MOVING_THROUGH_WALL_CORNERS)) {

    private val rand = SecureRandom()
    private val grid = Long2ObjectOpenHashMap<JPSNode2>(width * height)
    private val keys: LongArray = LongArray(width.coerceAtLeast(height) * width.coerceAtLeast(height))


    private fun addNode(node: JPSNode2) {
        val hash = hash(node)
        keys[node.y * height + node.x] = hash
        grid[hash] = node
    }

    fun getNode(x: Double, y: Double): JPSNode2? {
        return null
    }

    fun getNode(x: Int, y: Int): JPSNode2? {
        return if (!isInsideGrid(x, y)) null else grid[keys[y * height + x]]
    }


    fun isWalkableAt(x: Int, y: Int): Boolean {
        return isInsideGrid(x, y) && grid[keys[y * height + x]]?.state != NodeState.WALL
    }

    private fun isInsideGrid(x: Int, y: Int): Boolean {
        return x in 0 until width && y >= 0 && y < height
    }

    /**
     * Calculates movement cost from one node to another. This is currently hard coded to be 1 for hor/vert, and
     * sqrt(2) for diagonal movement, resulting in a minimum D cost of 1
     * @param n1
     * @param n2
     * @return
     */
    fun getMovementCost(n1: JPSNode2, n2: JPSNode2): Double {
        val dx = Math.abs(n1.x - n2.x).toDouble()
        val dy = Math.abs(n1.y - n2.y).toDouble()

        return if (dx == 0.0 || dy == 0.0) 1.0 else Math.sqrt(2.0)
    }

    private fun hash(node: JPSNode2): Long {
        var hash = rand.nextLong()

        hash = hash xor node.x.toLong()
        hash = hash xor node.y.toLong()

        return hash
    }

    fun draw(side: Int): Image {
        println("Grid draw")
        val image = WritableImage(this.width, this.height)
        println("$width $height")

        val pw = image.pixelWriter

        for (y in 0 until this.height) {
            for (x in 0 until this.width) {
                pw.setColor(x, y, NodeState.color(grid[keys[y * this.height + x]]!!.state))
            }
        }

        return resample(image, side)
    }

    companion object {

        fun create(vertices: Array<IntArray>): AbstractSearchSpace {
            val grid = JPSGrid(vertices[0].size, vertices.size)

            for (y in vertices.indices) {
                for (x in vertices[0].indices) {
                    grid.addNode(JPSNode2(x, y, NodeState.parse(vertices[y][x])))
                }
            }

            return grid
        }

        fun create(vertices: Array<Array<Int>>): AbstractSearchSpace {
            val grid = JPSGrid(vertices[0].size, vertices.size)

            for (y in vertices.indices) {
                for (x in vertices[0].indices) {
                    grid.addNode(JPSNode2(x, y, NodeState.parse(vertices[y][x])))
                }
            }

            return grid
        }
    }

    //    @Override
    //    public AbstractSearchSpace copy() {
    //        return JPSGrid.create(getMapData(), options);
    //    }
}

