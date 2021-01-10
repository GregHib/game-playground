package world.gregs.game.playground.pathfinding.bfs.unsafe

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import sun.misc.Unsafe
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class UnsafeFifoCircularQueueTest {

    private val length = 4
    lateinit var queue: UnsafeFifoCircularQueue

    lateinit var unsafe: Unsafe

    @BeforeEach
    fun setup() {
        unsafe = Memory.getUnsafe()
        queue = UnsafeFifoCircularQueue(unsafe, length)
        queue.setDefault(-1)
        queue.reset()
    }

    @Test
    fun `Poll values first in first out`() {
        queue.add(5)
        queue.add(2)
        queue.add(1)

        assertEquals(5, queue.poll())
        assertEquals(2, queue.poll())
        assertEquals(1, queue.poll())
    }

    @Test
    fun `Peek always returns first value`() {
        queue.add(4)
        queue.add(2)
        assertEquals(4, queue.peek())
        assertEquals(4, queue.peek())
        assertEquals(4, queue.poll())
        assertEquals(2, queue.peek())
        assertEquals(2, queue.peek())
    }

    @Test
    fun `Add more than max loops around to the start`() {
        queue.add(1)
        queue.add(2)
        queue.add(3)
        queue.add(4)
        queue.add(5)
        assertEquals(5, queue.poll())
        assertEquals(2, queue.poll())
        assertEquals(3, queue.poll())
        assertEquals(4, queue.poll())
    }

    @Test
    fun `Poll more than max loops around to the start`() {
        queue.add(1)
        assertEquals(1, queue.poll())
        queue.add(2)
        queue.add(3)
        queue.add(4)
        queue.add(5)
        queue.add(6)
        assertEquals(6, queue.poll())
        assertEquals(3, queue.poll())
        assertEquals(4, queue.poll())
    }

    @Test
    fun `If reader laps writer then queue is empty`() {
        queue.add(1)
        assertTrue(queue.isNotEmpty())
        assertEquals(1, queue.poll())
        assertFalse(queue.isNotEmpty())
    }

    @AfterEach
    fun tearDown() {
        queue.free()
    }

}