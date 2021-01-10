package world.gregs.game.playground.pathfinding.bfs

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import sun.misc.Unsafe
import world.gregs.game.playground.pathfinding.bfs.UnsafeBreadthFirstSearch.Companion.getX
import world.gregs.game.playground.pathfinding.bfs.UnsafeBreadthFirstSearch.Companion.getY
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class UnsafeBreadthFirstSearchTest {
    lateinit var bfs: UnsafeBreadthFirstSearch
    lateinit var collisions: Collisions
    lateinit var target: TargetPredicate

    val width = 10
    val height = 10

    @BeforeEach
    fun setup() {
        collisions = mockk(relaxed = true)
        bfs = UnsafeBreadthFirstSearch(collisions, width, height)
        target = target(width, height) { _, _ -> false }
    }

    fun target(targetX: Int, targetY: Int, targetWidth: Int = 1, targetHeight: Int = 1, reached: (Int, Int) -> Boolean) = object : TargetPredicate {
        override val x: Int = targetX
        override val y: Int = targetY

        override fun reached(x: Int, y: Int, z: Int): Boolean {
            return reached.invoke(x, y)
        }

        override fun distance(x: Int, y: Int, z: Int): Int {
            val deltaX = when {
                targetX > x -> targetX - x
                targetX + targetWidth <= x -> x - (targetX + targetWidth) + 1
                else -> 0
            }
            val deltaY = when {
                targetY > y -> targetY - y
                targetY + targetHeight <= y -> y - (targetY + targetHeight) + 1
                else -> 0
            }
            return deltaX * deltaX + deltaY * deltaY
        }
    }

    @Test
    fun `Search is reset before each call`() {
        bfs.search(target, 0, 0)
        assertTrue(bfs.visited(1, 1))

        every { collisions.blocked(any(), any(), 1, 1, any()) } returns true
        bfs.search(target, 0, 0)
        assertFalse(bfs.visited(1, 1))
    }

    @Test
    fun `Frontier stops when reached target`() {
        val target = target(0, 3) { x, y -> x == 0 && y == 3 }
        val result = bfs.search(target, 0, 0, limit = 4)
        assertEquals(0, getX(result))
        assertEquals(3, getY(result))
        assertTrue(bfs.visited(3, 0))
        assertFalse(bfs.visited(5, 0))
    }

    @Test
    fun `Limit search length`() {
        bfs.search(target, 0, 0, limit = 4)
        assertTrue(bfs.visited(4, 0))
        assertFalse(bfs.visited(5, 0))
    }

    @Test
    fun `Obstructions aren't visited`() {
        every { collisions.blocked(any(), any(), 1, 1, any()) } returns true
        bfs.search(target, 0, 0)
        assertFalse(bfs.visited(1, 1))
        assertEquals(0, bfs.getDistance(1, 1))
        assertEquals(2, bfs.getDistance(1, 0))
        assertEquals(3, bfs.getDistance(2, 0))
        assertEquals(3, bfs.getDistance(2, 1))
        assertEquals(4, bfs.getDistance(2, 2))
        assertEquals(5, bfs.getDistance(3, 3))
    }

    @Test
    fun `Can do two searches back to back`() {
        every { collisions.blocked(any(), any(), 1, 1, any()) } returns true
        var targetX = 3
        var targetY = 3
        var target = target(targetX, targetY) { x, y -> x == targetX && y == targetY }
        bfs.search(target, 0, 0)
        targetX = 4
        targetY = 4
        target = target(targetX, targetY) { x, y -> x == targetX && y == targetY }
        bfs.search(target, 0, 0)
    }

    @Test
    fun `Multiple searches don't overlap`() {
        bfs.search(target, 0, 0)
        assertEquals(10, bfs.getDistance(9, 9))
        bfs.search(target, 9, 9)
        assertEquals(10, bfs.getDistance(0, 0))
        bfs.search(target, 0, 9)
        assertEquals(10, bfs.getDistance(9, 0))
        bfs.search(target, 9, 0)
        assertEquals(10, bfs.getDistance(0, 9))
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
        val target = target(targetX, targetY) { _, _ -> false }
        bfs.search(target, 0, 0)
        val result = bfs.searchPartial(target, 1, 1)
        assertEquals(3, UnsafeBreadthFirstSearch.getX(result))
        assertEquals(2, UnsafeBreadthFirstSearch.getY(result))
    }

    @Test
    fun `Backtrace path from target to start`() {
        every { collisions.blocked(any(), any(), 1, 1, any()) } returns true
        val targetX = 2
        val targetY = 2
        val target = target(targetX, targetY) { x, y -> x == targetX && y == targetY }
        val result = bfs.search(target, 0, 0)
        println(UnsafeBreadthFirstSearch.getX(result))
        println(UnsafeBreadthFirstSearch.getY(result))
        val block: (Int) -> Unit = mockk(relaxed = true)
        bfs.backtrace(result, block)
        verifyOrder {
            block.invoke(2)
            block.invoke(4)
            block.invoke(0)
            block.invoke(any()) wasNot Called
        }
    }

    @AfterEach
    fun tearDown() {
        bfs.free()
    }

    private fun UnsafeBreadthFirstSearch.printDist() {
        for (y in height - 1 downTo 0) {
            for (x in 0 until width) {
                val distance = if (bfs.visited(x, y)) bfs.getDistance(x, y) else 0
                print("$distance${if (distance in 0..9) " " else ""} ")
            }
            println()
        }
    }
    private fun UnsafeBreadthFirstSearch.printDir() {
        for (y in height - 1 downTo 0) {
            for (x in 0 until width) {
                val distance = if (bfs.visited(x, y)) bfs.getDirection(x, y) else 0
                print("$distance${if (distance in 0..9) " " else ""} ")
            }
            println()
        }
    }
}