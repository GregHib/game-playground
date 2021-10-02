package world.gregs.game.playground.ai.gen

import kotlin.random.Random

private typealias Genome = Array<Char>

/**
 * Uses a genetic algorithm to solve the shakespeare's infinite monkey scenario far more efficiently than probabilities of using brute force.
 */
object GeneticAlgorithm {

    private const val TARGET = "To be or not to be."
    private const val GENOME_SIZE = TARGET.length
    private const val MUTATION_RATE = 0.01
    private const val POPULATION_SIZE = 500
    private val validCharacters = run {
        val list = mutableListOf<Char>()
        list.addAll('a'..'z')
        list.addAll('A'..'Z')
        list.add(' ')
        list.add('.')
        return@run list
    }

    private val population = Array(POPULATION_SIZE) { Array(GENOME_SIZE) { validCharacters.random() } }
    private val matingPool = mutableMapOf<Genome, Double>()

    private fun Map<Genome, Double>.random(): Genome {
        var total = values.sum()
        if (total == 0.0) {
            total = -1.0
        }
        val random = Random.nextDouble(total)
        var sum = 0.0
        return entries.first {
            sum += it.value
            sum > random
        }.key
    }

    @JvmStatic
    fun main(args: Array<String>) {
        var generations = 0
        var closest: Genome = Genome(GENOME_SIZE) { '-' }
        loop@ while (true) {
            var total = 0.0
            matingPool.clear()
            var highestFitness = 0.0
            for (genes in population) {
                val fitness = fitness(genes)
                if (fitness > highestFitness) {
                    highestFitness = fitness
                    closest = genes
                }
                matingPool[genes] = matingPool.getOrDefault(genes, 0.0) + fitness
                if (fitness == 1.0) {
                    break@loop
                }
                total += fitness
            }
            println("Highest: '${closest.joinToString("")}' fitness: $highestFitness, average: ${total / population.size.toDouble() * 100.0}")
            generations++
            reproduce()
        }
        println("Found after $generations generations.")
    }

    private fun fitness(genes: Genome): Double {
        return genes.withIndex().count { (index, char) -> char == TARGET[index] } / GENOME_SIZE.toDouble()
    }

    private fun reproduce() {
        for (index in population.indices) {
            val a = matingPool.random()
            val b = matingPool.random()
            population[index] = mate(a, b)
        }
    }

    private fun mate(a: Genome, b: Genome): Genome {
        val midpoint = a.indices.random()
        return Array(a.size) { i ->
            if (Random.nextDouble() < MUTATION_RATE) {
                validCharacters.random()
            } else if (i > midpoint) {
                a[i]
            } else {
                b[i]
            }
        }
    }
}