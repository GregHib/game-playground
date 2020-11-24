package world.gregs.game.playground.ai.property

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LongPropertyTest {

    @Test
    fun `add two longs`() {
        val a = LongProperty(1)
        val b = LongProperty(1)
        val c = a + b
        b.set(2)
        assertEquals(3, c.get())
    }

    @Test
    fun `minus two longs`() {
        val a = LongProperty(1)
        val b = LongProperty(1)
        val c = a - b
        b.set(2)
        assertEquals(-1, c.get())
    }

    @Test
    fun `multiply two longs`() {
        val a = LongProperty(1)
        val b = LongProperty(1)
        val c = a * b
        b.set(2)
        assertEquals(2, c.get())
    }

    @Test
    fun `divide two longs`() {
        val a = LongProperty(4)
        val b = LongProperty(2)
        val c = a / b
        b.set(4)
        assertEquals(1, c.get())
    }

    @Test
    fun `remainder of two longs`() {
        val a = LongProperty(1)
        val b = LongProperty(1)
        val c = a % b
        b.set(2)
        assertEquals(1, c.get())
    }

    @Test
    fun `add long to utility`() {
        val a = LongProperty(1)
        val c = a + 2
        a.set(2)
        assertEquals(4, c.get())
    }

    @Test
    fun `minus long`() {
        val a = LongProperty(1)
        val c = a - 2
        a.set(2)
        assertEquals(0, c.get())
    }

    @Test
    fun `multiply long`() {
        val a = LongProperty(1)
        val c = a * 2
        a.set(2)
        assertEquals(4, c.get())
    }

    @Test
    fun `divide long`() {
        val a = LongProperty(8)
        val c = a / 4
        a.set(12)
        assertEquals(3, c.get())
    }

    @Test
    fun `remainder of long`() {
        val a = LongProperty(1)
        val c = a % 2
        a.set(3)
        assertEquals(1, c.get())
    }
}