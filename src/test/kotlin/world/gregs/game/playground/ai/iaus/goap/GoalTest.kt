package world.gregs.game.playground.ai.iaus.goap

internal class GoalTest {

    /*lateinit var reasoner: Reasoner

    @BeforeEach
    fun setup() {
        reasoner = Reasoner(Agent(0, 0))
    }

    @Test
    fun `Make plan to reach a goal`() {
        val state: WorldState = mutableMapOf("boolean" to false)
        val goal: WorldState = mutableMapOf("boolean" to true)
        val a1 = addAction(preconditions = mutableMapOf("boolean" to false), effects = mutableMapOf("boolean" to true))
        // When
        val success = reasoner.plan(state, goal)
        // Then
        assertTrue(success)
        assertArrayEquals(arrayOf(a1), reasoner.plan.toTypedArray())
    }

    @Test
    fun `No plan if no valid actions`() {
        val state: WorldState = mutableMapOf("boolean" to true)
        val goal: WorldState = mutableMapOf("boolean" to true)
        addAction(preconditions = mutableMapOf("boolean" to false), effects = mutableMapOf("boolean" to true))
        // When
        val success = reasoner.plan(state, goal)
        // Then
        assertFalse(success)
    }

    @Test
    fun `Lowest cost plan is chosen`() {
        val state: WorldState = mutableMapOf("boolean" to false)
        val goal: WorldState = mutableMapOf("boolean" to true)
        addAction(cost = 1.0, preconditions = mutableMapOf("boolean" to false), effects = mutableMapOf("boolean" to true))
        val a2 = addAction(cost = 0.8, preconditions = mutableMapOf("boolean" to false), effects = mutableMapOf("boolean" to true))
        // When
        val success = reasoner.plan(state, goal)
        // Then
        assertTrue(success)
        assertArrayEquals(arrayOf(a2), reasoner.plan.toTypedArray())
    }

    private enum class TestEnum {
        One,
        Two
    }

    @Test
    fun `Multiple state changes`() {
        val state: WorldState = mutableMapOf("boolean" to false, "count" to 0, "enum" to TestEnum.One)
        val goal: WorldState = mutableMapOf("boolean" to true, "count" to 1, "enum" to TestEnum.Two)
        val action = addAction(effects = goal)
        // When
        val success = reasoner.plan(state, goal)
        // Then
        assertTrue(success)
        assertArrayEquals(arrayOf(action), reasoner.plan.toTypedArray())
    }

    private fun addAction(
        cost: Double = 1.0,
        preconditions: WorldState = mutableMapOf(),
        effects: WorldState = mutableMapOf()
    ) = PrimitiveAction(cost, preconditions, effects).apply {
        reasoner.usableActions.add(this)
    }*/

}