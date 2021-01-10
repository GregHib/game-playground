package world.gregs.game.playground.pathfinding.bfs.unsafe

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import sun.misc.Unsafe

internal class UnsafeIntArrayTest {

    private val length = 13
    lateinit var array: UnsafeIntArray

    lateinit var unsafe: Unsafe

    @BeforeEach
    fun setup() {
        unsafe = Memory.getUnsafe()
        array = UnsafeIntArray(unsafe, length)
    }

    @AfterEach
    fun tearDown() {
        array.free()
    }

    @Test
    fun `All values set`() {
        var counter = 0
        for (i in 0 until length) {
            array[i] = counter++
        }
        counter = 0
        for (i in 0 until length) {
            assertEquals(counter++, array[i])
        }
    }

    @Test
    fun `Reset all values`() {
        var counter = 0
        for (i in 0 until length) {
            array[i] = counter++
        }
        array.fill(-1)
        for (i in 0 until length) {
            assertEquals(-1, array[i])
        }
    }

    @Test
    fun `Set out of bounds exception`() {
        org.junit.jupiter.api.assertThrows<IndexOutOfBoundsException> {
            array[-10] = 10
        }
        org.junit.jupiter.api.assertThrows<IndexOutOfBoundsException> {
            array[length] = 10
        }
        org.junit.jupiter.api.assertThrows<IndexOutOfBoundsException> {
            array[length + 100] = 10
        }
    }

    @Test
    fun `Out of bounds value returns default`() {
        array.setDefault(-1)
        assertEquals(-1, array[-10])
        assertEquals(-1, array[length])
        assertEquals(-1, array[length + 100])
    }
}