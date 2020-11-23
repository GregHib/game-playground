package world.gregs.game.playground.spatial.grid

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import world.gregs.game.playground.Node

/**
 * https://www.redblobgames.com/pathfinding/grids/graphs.html
 */
internal class GridsAndGraphs {

    @Test
    fun `Grids in graph form`() {
        val allNodes = mutableListOf<Node>()
        repeat(20) { x ->
            repeat(10) { y ->
                allNodes.add(Node(x, y))
            }
        }

        allNodes shouldHaveSize 200
    }

    @Test
    fun `Regular neighbors`() {
        val allNodes = mutableListOf<Node>()
        repeat(20) { x ->
            repeat(10) { y ->
                allNodes.add(Node(x, y))
            }
        }

        fun neighbors(node: Node): MutableList<Node> {
            val dirs = arrayOf(
                Node(1, 0),
                Node(0, 1),
                Node(-1, 0),
                Node(0, -1)
            )
            val result = mutableListOf<Node>()
            for(dir in dirs) {
                val neighbor = Node(node.x + dir.x, node.y + dir.y)
                if(neighbor in allNodes) {
                    result.add(neighbor)
                }
            }
            return result
        }
        val neighbors = neighbors(allNodes.first())//0, 0

        neighbors shouldHaveSize 2
        neighbors[0] `should be equal to` Node(1, 0)
        neighbors[1] `should be equal to` Node(0, 1)
    }

    @Test
    fun `Rectangular neighbors`() {
        val allNodes = mutableListOf<Node>()
        repeat(20) { x ->
            repeat(10) { y ->
                allNodes.add(Node(x, y))
            }
        }

        fun neighbors(node: Node): MutableList<Node> {
            val dirs = arrayOf(
                Node(1, 0),
                Node(0, 1),
                Node(-1, 0),
                Node(0, -1)
            )
            val result = mutableListOf<Node>()
            for(dir in dirs) {
                val neighbor = Node(node.x + dir.x, node.y + dir.y)
                if(neighbor.x in 0..20 && neighbor.y in 0..10) {
                    result.add(neighbor)
                }
            }
            return result
        }
        val neighbors = neighbors(allNodes.first())//0, 0

        neighbors shouldHaveSize 2
        neighbors[0] `should be equal to` Node(1, 0)
        neighbors[1] `should be equal to` Node(0, 1)
    }
}