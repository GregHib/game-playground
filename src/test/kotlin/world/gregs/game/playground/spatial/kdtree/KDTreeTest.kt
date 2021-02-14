package world.gregs.game.playground.spatial.kdtree

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.awt.Point

internal class KDTreeTest {

    @Test
    fun `Kd tree nearest neighbour`() {
        val tree = KDTree(listOf(Point(1, 0)))
        assertEquals(Point(1, 0), tree.nearest(Point(10, 0)))
    }

    @Test
    fun `Nearest neighbour of two points`() {
        val tree = KDTree(listOf(Point(1, 0), Point(4, 0)))
        assertEquals(Point(4, 0), tree.nearest(Point(3, 0)))
    }

    @Test
    fun `Nearest neighbour of two equidistant points`() {
        val tree = KDTree(listOf(Point(1, 0), Point(3, 0)))
        assertEquals(Point(3, 0), tree.nearest(Point(2, 0)))
    }

    @Test
    fun `Nearest neighbour of two two-dimensional points`() {
        val tree = KDTree(listOf(Point(0, 2), Point(3, 1)))
        assertEquals(Point(3, 1), tree.nearest(Point(2, 2)))
    }

    @Test
    fun `Point on other planes aren't considered`() {
        val tree = KDTree(listOf(Point(0, 2)))
        assertEquals(Point(0, 2), tree.nearest(Point(0, 0)))
    }

    @Test
    fun `Invalid position returns null`() {
        val tree = KDTree(listOf())
        Assertions.assertNull(tree.nearest(Point(-1, -1)))
    }

    @Test
    fun `Nearest neighbour of multiple points`() {
        val tree = KDTree(listOf(
            Point(2, 3),
            Point(5, 4),
            Point(9, 6),
            Point(4, 7),
            Point(8, 1),
            Point(7, 2),
            Point(6, 3)
        ))
        assertEquals(Point(6, 3), tree.nearest(Point(7, 5)))
    }
}