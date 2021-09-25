package world.gregs.game.playground.ai.nn

import kotlin.random.Random

object LearnAddition {
    @JvmStatic
    fun main(args: Array<String>) {
        val layer1 = NeuronLayer(4, 2)
        val layer2 = NeuronLayer(1, 4)
        val net = NeuralNetwork(layer1, layer2)

        val (inputs, outputs) = createTrainingSet(20)

        println("Training network...")
        val start = System.currentTimeMillis()
        net.train(inputs, outputs, 10000)
        println("Training completed in ${System.currentTimeMillis() - start}ms")

        println("Layer 1 weights")
        println(layer1.weights.map { it.toList() })

        println("Layer 2 weights")
        println(layer2.weights.map { it.toList() })

        predict(net, arrayOf(doubleArrayOf(0.25, 0.1)))
        predict(net, arrayOf(doubleArrayOf(0.99, -0.33)))
        predict(net, arrayOf(doubleArrayOf(0.2, 0.2)))
    }

    private fun predict(network: NeuralNetwork, input: Array<DoubleArray>) {
        network.input(input)
        println("Prediction on data: ${input.map { it.toMutableList().apply { add(it.sum()) } }}, actual -> ${network.outputLayer2[0][0]}")
    }

    private fun createTrainingSet(size: Int): Pair<Array<DoubleArray>, Array<DoubleArray>> {
        val inputs = Array(size) { DoubleArray(2) }
        val outputs = Array(size) { DoubleArray(1) }

        repeat(size) {
            val s1 = Random.nextDouble(0.5)
            val s2 = Random.nextDouble(0.5)
            inputs[it][0] = s1
            inputs[it][1] = s2
            outputs[it][0] = s1 + s2
        }
        return inputs to outputs
    }
}