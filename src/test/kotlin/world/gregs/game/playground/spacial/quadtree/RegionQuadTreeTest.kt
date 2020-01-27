package world.gregs.game.playground.spacial.quadtree

import org.junit.jupiter.api.Test
import world.gregs.game.playground.math.Point
import world.gregs.game.playground.math.Rectangle
import world.gregs.game.playground.spacial.quadtree.region.RegionQuadTree
import kotlin.random.Random

internal class RegionQuadTreeTest {

    @Test
    fun `QuadTree test`() {
        val width = 200
        val height = 200
        val boundary = Rectangle(0, 0, width, height)
        val qt = RegionQuadTree(boundary, 4)
        for(i in 0..4) {
            val p = Point(Random.nextInt(width), Random.nextInt(height))
            qt.insert(p)
        }
    }
}