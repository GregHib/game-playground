package world.gregs.game.playground.ai.prob

import world.gregs.game.playground.ai.iaus.PlayerAIView
import world.gregs.game.playground.ai.iaus.bot.behaviour.Behaviour
import world.gregs.game.playground.ai.iaus.bot.behaviour.BehaviourSet
import world.gregs.game.playground.ai.iaus.bot.consider
import world.gregs.game.playground.ai.iaus.scale

object Prediction {

    fun matchEnds(sequence: List<Int>, subList: List<Int>): Boolean {
        for (i in 0 until subList.lastIndex - 1) {
            if (sequence[sequence.lastIndex - i] != subList[subList.lastIndex - 1 - i]) {
                return false
            }
        }
        return true
    }

    private val sequence = mutableListOf<Int>()
    private val occurrences = mutableMapOf<List<Int>, Int>()
    private val behaviours = BehaviourSet<List<Int>, List<Int>>()

    fun predictNext(next: Int): Int {
        val all = mutableSetOf<List<Int>>()
        for (i in 0..sequence.size) {
            add(all, sequence.take(i))
            add(all, sequence.takeLast(i))
        }
        all.remove(emptyList())
        val sorted = all.sortedByDescending { it.size }
        val largest = sorted.firstOrNull()?.size?.toDouble() ?: 0.0
        val ordered = sorted.groupBy { it.size }
        sequence.add(next)
        behaviours.clear()
        occurrences.remove(emptyList())
        for ((_, group) in ordered) {
            val matches = group.filter { matchEnds(sequence, it) }
            if (matches.isEmpty()) {
                continue
            }
            val total = occurrences.values.sum().toDouble()
            behaviours.add(ListBehaviour(name = matches.joinToString(""),
                targets = { matches },
                considerations = setOf(
                    consider { _: List<Int>, list: List<Int> ->
                        list.size.toDouble().scale(0.0, largest)
                    },
                    consider { _: List<Int>, list: List<Int> ->
                        occurrences[list]!! / total
                    }
                )))
        }

        val choice = behaviours.select(sequence)
        return choice?.target?.last() ?: -1
    }

    private fun add(all: MutableSet<List<Int>>, list: List<Int>) {
        if (all.contains(list)) {
            occurrences[list] = occurrences.getOrDefault(list, 0) + 1
        } else {
            occurrences[list] = 1
            all.add(list)
        }
    }

    data class ListBehaviour(
        override val name: String,
        override val considerations: Set<(List<Int>, List<Int>) -> Double> = setOf(),
        override val targets: (List<Int>) -> List<List<Int>> = self,
        override val momentum: Double = 1.25,
        override val weight: Double = 1.0
    ) : Behaviour<List<Int>, List<Int>> {
        companion object {
            val self: (List<Int>) -> List<List<Int>> = { list -> listOf(list) }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {


        PlayerAIView.debug = false
        val sequence = listOf(0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0)

        for (i in sequence) {
            println("$i predict: ${predictNext(i)}")
        }
    }

    class TreeNode(
        val value: Int,
        val branches: MutableMap<Int, TreeNode> = mutableMapOf(),
        val occurrence: MutableMap<Int, Int> = mutableMapOf(),
        val probabilities: MutableMap<Int, Double> = mutableMapOf()
    ) {

        constructor(value: Int) : this(value, mutableMapOf(), mutableMapOf(value to 1), mutableMapOf(value to 1.0))

        fun calcProbabilities() {
            probabilities.clear()
            val total = occurrence.values.sum().toDouble()
            for ((value, count) in occurrence) {
                val probability = count / total
                probabilities[value] = probability
            }
        }

        override fun toString(): String {
            val prefix = "Node($value) -> "
            val joinToString = branches.map { it.toString() }.joinToString("\n ${" ".repeat(prefix.split("\n").maxOf { it.length })}")
            return "$prefix$joinToString"
        }
    }

    var root: TreeNode = TreeNode(-1, mutableMapOf())
    var currentBranch = root

    /**
     * @return <Value, likelihood>
     */
    private fun predict(next: Int): Map<Int, Double> {
        val previous = currentBranch
        val branch = currentBranch.branches.getOrPut(next) { TreeNode(next) }

        if (previous.value != next && previous.value != -1) {
            currentBranch = root
        } else {
            currentBranch = branch
        }
        previous.occurrence[next] = previous.occurrence.getOrDefault(next, 0) + 1
        previous.calcProbabilities()
        return previous.probabilities
    }
}