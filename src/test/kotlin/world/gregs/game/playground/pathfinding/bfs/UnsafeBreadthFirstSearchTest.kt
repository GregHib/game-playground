package world.gregs.game.playground.pathfinding.bfs

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import sun.misc.Unsafe
import world.gregs.game.playground.euclidean
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class UnsafeBreadthFirstSearchTest {
    lateinit var bfs: UnsafeBreadthFirstSearch
    lateinit var collisions: Collisions
    lateinit var unsafe: Unsafe
    lateinit var target: TargetPredicate

    val width = 10
    val height = 10

    @BeforeEach
    fun setup() {
        unsafe = UnsafeBreadthFirstSearch.getUnsafe()
        collisions = mockk(relaxed = true)
        bfs = UnsafeBreadthFirstSearch(collisions, unsafe, width, height)
        target = target({ _, _ -> false }, { _, _ -> -1 })
    }

    fun target(reached: (Int, Int) -> Boolean, distance: (Int, Int) -> Int) = object : TargetPredicate {
        override fun reached(x: Int, y: Int, z: Int): Boolean {
            return reached.invoke(x, y)
        }

        override fun distance(x: Int, y: Int, z: Int): Int {
            return distance.invoke(x, y)
        }
    }

    @Test
    fun `Search is reset before each call`() {
        bfs.search(target, 0, 0)
        assertEquals(2, bfs.getCost(1, 1))

        every { collisions.blocked(any(), any(), 1, 1, any()) } returns true
        bfs.search(target, 0, 0)
        assertEquals(0, bfs.getCost(1, 1))
    }

    @Test
    fun `Obstructions aren't visited`() {
        every { collisions.blocked(any(), any(), 1, 1, any()) } returns true
        bfs.search(target, 0, 0)
        assertFalse(bfs.visited(1, 1))
        assertEquals(0, bfs.getCost(1, 1))
        assertEquals(3, bfs.getCost(1, 0))
        assertEquals(5, bfs.getCost(2, 0))
        assertEquals(4, bfs.getCost(2, 1))
        assertEquals(6, bfs.getCost(2, 2))
        assertEquals(7, bfs.getCost(3, 3))
    }

    @Test
    fun `Diagonals are cheaper cost than cardinal`() {
        every { collisions.blocked(any(), any(), 1, 1, any()) } returns true
        bfs.search(target, 5, 5)
        assertTrue(bfs.getCost(4, 4) < bfs.getCost(4, 5))
    }

    @Test
    fun `Out of bounds aren't visited`() {
        bfs.search(target, 5, 5)
        assertFalse(bfs.visited(15, 15))
    }

    @Test
    fun `Partial returns the closest tile when unreachable`() {
        repeat(10) {
            every { collisions.blocked(any(), any(), it, 3, any()) } returns true
        }
        val targetX = 3
        val targetY = 5
        val target = target({ _, _ -> false }, { x, y ->
            val deltaX = if (targetX > x) targetX - x else x - targetX
            val deltaY = if (targetY > y) targetY - y else y - targetY
            deltaX * deltaX + deltaY * deltaY
        })
        val result = bfs.searchPartial(target, 0, 0)
        assertEquals(3, UnsafeBreadthFirstSearch.getX(result))
        assertEquals(2, UnsafeBreadthFirstSearch.getY(result))
    }

    @AfterEach
    fun tearDown() {
        bfs.clear()
    }

    private fun UnsafeBreadthFirstSearch.print() {
        for (y in height - 1 downTo 0) {
            for (x in 0 until width) {
                print("${bfs.getCost(x, y)}${if (bfs.getCost(x, y) in 0..9) " " else ""} ")
            }
            println()
        }
    }
}