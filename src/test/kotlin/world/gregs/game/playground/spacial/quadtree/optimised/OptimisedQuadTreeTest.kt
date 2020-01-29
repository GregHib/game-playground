package world.gregs.game.playground.spacial.quadtree.optimised

import junitparams.Parameters
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import world.gregs.game.playground.spacial.quadtree.QuadTree

@RunWith(Parameterized::class)
internal class OptimisedQuadTreeTest {

    lateinit var quadTree: QuadTree

    private var capacity = 4

    @BeforeEach
    fun setup() {
    }

    @Test
    @Parameters(
        "1",
        "4"
    )
    fun `Subdivides when exceeds capacity ${capacity}`() {
        //Given
        //When

        //Then
    }
}