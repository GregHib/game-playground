package world.gregs.game.playground.pathfinding.bfs.unsafe

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import sun.misc.Unsafe

internal class Unsafe2DIntArrayTest {

    private val width = 10
    private val height = 15
    lateinit var array: Unsafe2DIntArray

    lateinit var unsafe: Unsafe

    @BeforeEach
    fun setup() {
        unsafe = Memory.getUnsafe()
        array = Unsafe2DIntArray(unsafe, width, height)
    }

    @AfterEach
    fun tearDown() {
        array.free()
    }

    @Test
    fun `All values set`() {
        var counter = 0
        for (x in 0 until width) {
            for (y in 0 until height) {
                array[x, y] = counter++
            }
        }
        counter = 0
        for (x in 0 until width) {
            for (y in 0 until height) {
                assertEquals(counter++, array[x, y])
            }
        }
    }

    @Test
    fun `Fill all values`() {
        var counter = 0
        for (x in 0 until width) {
            for (y in 0 until height) {
                array[x, y] = counter++
            }
        }
        array.fill(-1)
        for (x in 0 until width) {
            for (y in 0 until height) {
                assertEquals(-1, array[x, y])
            }
        }
    }

    @Test
    fun `Set x out of bounds exception`() {
        org.junit.jupiter.api.assertThrows<IndexOutOfBoundsException> {
            array[-10, 0] = 10
        }
        org.junit.jupiter.api.assertThrows<IndexOutOfBoundsException> {
            array[width, 0] = 10
        }
        org.junit.jupiter.api.assertThrows<IndexOutOfBoundsException> {
            array[width + 100, 0] = 10
        }
    }

    @Test
    fun `Set y out of bounds exception`() {
        org.junit.jupiter.api.assertThrows<IndexOutOfBoundsException> {
            array[0, -10] = 10
        }
        org.junit.jupiter.api.assertThrows<IndexOutOfBoundsException> {
            array[0, height] = 10
        }
        org.junit.jupiter.api.assertThrows<IndexOutOfBoundsException> {
            array[0, height + 100] = 10
        }
    }

    @Test
    fun `Out of bounds x value returns default`() {
        array.setDefault(-1)
        assertEquals(-1, array[-10, 0])
        assertEquals(-1, array[width, 0])
        assertEquals(-1, array[width + 100, 0])
    }

    @Test
    fun `Out of bounds y value returns default`() {
        array.setDefault(-1)
        assertEquals(-1, array[0, -10])
        assertEquals(-1, array[0, height])
        assertEquals(-1, array[0, height + 100])
    }
}