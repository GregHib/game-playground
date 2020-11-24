package world.gregs.game.playground.ai.iaus

internal class ActionTest {

    /*@Test
    fun `Score combines multiple consideration values`() {
        val one = entity()
        val consideration1: Consideration = { _, _ -> 0.5 }
        val consideration2: Consideration = { _, _ -> 0.5 }
        val action = action(targets = { emptyList() }, setOf(consideration1, consideration2))
        val agent = Agent(0, 0)
        // When
        val choice = action.score(agent, one)
        // Then
        assertEquals(0.5, choice, 0.12)
    }

    @Test
    fun `Score gains momentum if agent has same last goal`() {
        val one = entity()
        val consideration: Consideration = { _, _ -> 0.4 }
        val action = action(targets = { emptyList() }, setOf(consideration))
        val agent = Agent(0, 0)
        agent.reasoner.lastBehaviour = action
        // When
        val choice = action.score(agent, one)
        // Then
        assertEquals(0.5, choice)
    }

    @Test
    fun `Any consideration as zero returns zero`() {
        val one = entity()
        val consideration1: Consideration = { _, _ -> 0.4 }
        val consideration2: Consideration = { _, _ -> 0.0 }
        val action = action(targets = { emptyList() }, setOf(consideration1, consideration2))
        val agent = Agent(0, 0)
        // When
        val choice = action.score(agent, one)
        // Then
        assertEquals(0.0, choice)
    }

    @Test
    fun `Highest scoring target selected`() {
        val one = entity()
        val two = entity()
        val targets = listOf(one, two)
        val consideration: Consideration<Entity> = { _, e -> if (e == two) 0.8 else 0.5 }
        val action = action(targets = { targets }, setOf(consideration))
        val agent = Agent(0, 0)
        // When
        val choice = action.getHighestTarget(agent, 0.0)
        // Then
        assertNotNull(choice)
        assertEquals(action, choice!!.behaviour)
        assertEquals(two, choice.target)
        assertEquals(0.8, choice.score)
    }

    @Test
    fun `Target ignored if score isn't higher`() {
        val one = entity()
        val targets = listOf(one)
        val consideration: Consideration<Entity> = { _, _ -> 0.5 }
        val action = action(targets = { targets }, setOf(consideration))
        val agent = Agent(0, 0)
        // When
        val choice = action.getHighestTarget(agent, 0.6)
        // Then
        assertNull(choice)
    }

    @Test
    fun `Goal ignored if highest score is great than weight`() {
        val one = entity()
        val targets = listOf(one)
        val consideration: Consideration<Entity> = { _, _ -> 0.5 }
        val action = action(targets = { targets }, setOf(consideration), 0.7)
        val agent = Agent(0, 0)
        // When
        val choice = action.getHighestTarget(agent, 0.8)
        // Then
        assertNull(choice)
    }

    private fun entity() = object : Entity {
        override var state: WorldState = mutableMapOf()
    }

    private fun <T : Entity> action(targets: (Agent) -> List<T>, considerations: Set<Consideration<T>>, weight: Double = 1.0, momentum: Double = 1.25) = object : Behaviour<T>() {
        override val targets: (Agent) -> List<T> = targets
        override val considerations: Set<Consideration<T>> = considerations
        override val momentum: Double = momentum
        override val weight: Double = weight
    }*/
}