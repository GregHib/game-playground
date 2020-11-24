package world.gregs.game.playground.ai.property

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DoublePropertyTest {

    @Test
    fun `add two doubles`() {
        val a = DoubleProperty(1.0)
        val b = DoubleProperty(1.0)
        val c = a + b
        b.set(2.0)
        assertEquals(3.0, c.get())
    }

    @Test
    fun `minus two doubles`() {
        val a = DoubleProperty(1.0)
        val b = DoubleProperty(1.0)
        val c = a - b
        b.set(2.0)
        assertEquals(-1.0, c.get())
    }

    @Test
    fun `multiply two doubles`() {
        val a = DoubleProperty(1.0)
        val b = DoubleProperty(1.0)
        val c = a * b
        b.set(2.0)
        assertEquals(2.0, c.get())
    }

    @Test
    fun `divide two doubles`() {
        val a = DoubleProperty(2.0)
        val b = DoubleProperty(1.0)
        val c = a / b
        b.set(4.0)
        assertEquals(0.5, c.get())
    }

    @Test
    fun `remainder of two doubles`() {
        val a = DoubleProperty(1.0)
        val b = DoubleProperty(1.0)
        val c = a % b
        b.set(2.0)
        assertEquals(1.0, c.get())
    }

    @Test
    fun `add double to utility`() {
        val a = DoubleProperty(1.0)
        val c = a + 2.0
        a.set(2.0)
        assertEquals(4.0, c.get())
    }

    @Test
    fun `minus double`() {
        val a = DoubleProperty(1.0)
        val c = a - 2.0
        a.set(2.0)
        assertEquals(0.0, c.get())
    }

    @Test
    fun `multiply double`() {
        val a = DoubleProperty(1.0)
        val c = a * 2.0
        a.set(2.0)
        assertEquals(4.0, c.get())
    }

    @Test
    fun `divide double`() {
        val a = DoubleProperty(1.0)
        val c = a / 4.0
        a.set(2.0)
        assertEquals(0.5, c.get())
    }

    @Test
    fun `remainder of double`() {
        val a = DoubleProperty(1.0)
        val c = a % 2.0
        a.set(3.0)
        assertEquals(1.0, c.get())
    }

    @Test
    fun `rescale out of bounds`() {
        val a = DoubleProperty(101.0)
        val c = a.rescale(0.0, 100.0)
        assertEquals(1.0, c.get())
    }

    @Test
    fun `rescale value`() {
        val a = DoubleProperty(50.0)
        val c = a.rescale(0.0, 100.0)
        assertEquals(0.5, c.get())
    }
}