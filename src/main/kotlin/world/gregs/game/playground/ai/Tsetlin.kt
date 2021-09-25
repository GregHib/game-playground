package world.gregs.game.playground.ai

import kotlin.random.Random


class Tsetlin(stateDepth: Int, currentState: Int) {


    var firstState: Int

    init {
        val state = 0
        val action = when {
            currentState <= stateDepth -> 1
            currentState > stateDepth && currentState <= stateDepth * 2 -> 2
            currentState > stateDepth * 2 && currentState <= stateDepth * 3 -> 3
            currentState > stateDepth * 3 && currentState <= stateDepth * 4 -> 4
            currentState > stateDepth * 4 && currentState <= stateDepth * 5 -> 5
            currentState > stateDepth * 5 && currentState <= stateDepth * 6 -> 6
            currentState > stateDepth * 6 && currentState <= stateDepth * 7 -> 7
            else -> 8
        }
        val result = environment(action)

        firstState = when (result) {
            0 -> reward(currentState)
            1 -> penalty(stateDepth, currentState)
            else -> state
        }
    }

    fun environment(action: Int): Int {
        val r = Random(0)
        val noise: Double = r.nextDouble()
        val strength = 3 * action / 2 + noise

        //0 reward, 1 penalty
        return if (strength < highestStrength) {
            1
        } else {
            highestStrength = strength
            0
        }
    }

    fun reward(currentState: Int): Int {
        return if (currentState == 1 || currentState % 5 == 1) {
            currentState
        } else {
            currentState - 1
        }
    }

    fun penalty(stateDepth: Int, currentState: Int): Int {
        return when (currentState) {
            stateDepth -> 2 * stateDepth
            2 * stateDepth -> stateDepth
            else -> currentState + 1
        }
    }


    companion object {

        var states = 40
        var action = 8
        var experiments = 100
        var highestStrength = 0.0
        var stateDepth = states / action

        @JvmStatic
        fun main(args: Array<String>) {
            val start: Long = System.currentTimeMillis()
            val r = Random(0)
            val numberOfIterations = 10000
            var a1 = 0
            var a2 = 0
            var a3 = 0
            var a4 = 0
            var a5 = 0
            var a6 = 0
            var a7 = 0
            var a8 = 0
            for (i in 0 until experiments) {
                var currentState = r.nextInt(24 - 1 + 1) + 1
                var iteration = 0
                while (iteration < numberOfIterations) {
                    iteration++
                    val t = Tsetlin(stateDepth, currentState)
                    currentState = t.firstState
                    if (currentState <= stateDepth) {
                        if (iteration > 9900) {
                            a1++
                            print("1")
                        }
                    } else if (currentState > stateDepth && currentState <= stateDepth * 2) {
                        if (iteration > 9900) {
                            a2++
                            print("2")
                        }
                    } else if (currentState > stateDepth * 2 && currentState <= stateDepth * 3) {
                        if (iteration > 9900) {
                            a3++
                            print("3")
                        }
                    } else if (currentState > stateDepth * 3 && currentState <= stateDepth * 4) {
                        if (iteration > 9900) {
                            a4++
                            print("4")
                        }
                    } else if (currentState > stateDepth * 4 && currentState <= stateDepth * 5) {
                        if (iteration > 9900) {
                            a5++
                            print("5")
                        }
                    } else if (currentState > stateDepth * 5 && currentState <= stateDepth * 6) {
                        if (iteration > 9900) {
                            a6++
                            print("6")
                        }
                    } else if (currentState > stateDepth * 6 && currentState <= stateDepth * 7) {
                        if (iteration > 9900) {
                            a7++
                            print("7")
                        }
                    } else {
                        if (iteration > 9900) {
                            a8++
                            print("8")
                        }
                    }
                }
            }
            val end: Long = System.currentTimeMillis()
            val accuracy = a8 / (a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8)
            println("\nAction 1: ${a1 / 100}")
            println("Action 2: ${a2 / 100}")
            println("Action 3: ${a3 / 100}")
            println("Action 4: ${a4 / 100}")
            println("Action 5: ${a5 / 100}")
            println("Action 6: ${a6 / 100}")
            println("Action 7: ${a7 / 100}")
            println("Action 8: ${a8 / 100}")
            println("\nAccuracy: $accuracy")
            println("Time taken by function: " + (end - start) + " milliseconds")
        }
    }
}