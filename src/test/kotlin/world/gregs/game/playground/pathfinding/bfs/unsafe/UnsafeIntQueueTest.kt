package world.gregs.game.playground.pathfinding.bfs.unsafe

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import sun.misc.Unsafe
import world.gregs.game.playground.pathfinding.bfs.UnsafeBreadthFirstSearch
import kotlin.test.assertEquals

internal class UnsafeIntQueueTest {

    private val length = 4
    lateinit var queue: UnsafeIntQueue

    lateinit var unsafe: Unsafe

    @BeforeEach
    fun setup() {
        unsafe = UnsafeBreadthFirstSearch.getUnsafe()
        queue = UnsafeIntQueue(unsafe, length)
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

    @AfterEach
    fun tearDown() {
        queue.clear()
    }

}