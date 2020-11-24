package world.gregs.game.playground.ai.iaus.goap

import org.junit.jupiter.api.Test

internal class GoapTest {

    @Test
    fun `Blank test`() {
        var state = mapOf("storedTarget" to true)
        var position = null
        var target = "logs"

        /*
            Context stores the state, position and target

            Current state starts as the goal state, position as null, target as target
                loop1 - for all actions with effects which match the current state
                    undo effect on state
                        if state equals current state
                            break - plan found
                        else
                            repeat loop1


            Goal -
                Bank add logs
            Step 1 -
                Store(Param = Logs)
                    procedural = Nearest bank/depot
                    effect = Agent remove logs
                    effect = Bank add logs
            Step 2.1 -
                Chop-down(Target = Tree)
                    procedural = Nearest tree
                    effect = Agent add logs
                    effect = Target inactive
                    requires = Agent has axe
             Step 3.1 -
                Pick-up(Param = Axe)
                    procedural = Nearest axe on floor
                    effect = Agent add axe
                    requires = Axe on floor
             Step 3.2 -
                Buy(Param = Axe)
                    procedural = Nearest shop that sells axe
                    effect = Agent add axe
                    requires = stock
                    requires = Money x cost
             Step 3.3 -
                Kill(Param = Monster that drops axe)
                    procedural = Nearest monster that drops axe
                    effect = Axe on floor
                    requires = Lots of things
             Step 4 -
                Withdraw(Param = Money)
                    procedural = Nearest bank
                    effect = Agent add money
                    requires = Money in bank
             Step 2.2 -
                Buy(Param = Logs)
                    procedural = Nearest shop that sells logs
                    effect = Agent add logs
                    requires = stock
                    requires = Money x cost


         */
    }
}