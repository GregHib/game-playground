package world.gregs.game.playground.ai.dist

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import world.gregs.game.playground.ai.property.LongProperty

internal class ProbabilityListTest {

    private lateinit var list: ProbabilityList

    @BeforeEach
    fun setup() {
        list = ProbabilityList()
    }

    @Test
    fun `Add node increases weight`() {
        list.add(node(), 1L)
        list.add(node(), 2L)
        assertEquals(3L, list.total)
    }

    @Test
    fun `Remove node decreases weight`() {
        list.add(node(), 3L)
        val node = node()
        list.add(node, 2L)
        assertEquals(5L, list.total)
        list.remove(node)
        assertEquals(3L, list.total)
    }

    @Test
    fun `Sample always gives node`() {
        val node = node()
        list.add(node, 0L)
        assertEquals(node, list.sample())
    }

    @Test
    fun `Sample samples list nodes`() {
        val list2 = ProbabilityList()
        val node = node()
        list2.add(node, 0)
        list.add(list2, 0L)
        assertEquals(node, list.sample())
    }

    private fun node() = emptyArray<String>()
}