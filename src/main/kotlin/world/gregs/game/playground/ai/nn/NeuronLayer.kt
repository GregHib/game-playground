package world.gregs.game.playground.ai.nn

import world.gregs.game.playground.ai.nn.NeuralNetwork.Companion.addition
import world.gregs.game.playground.ai.nn.NeuralNetwork.Companion.apply
import world.gregs.game.playground.ai.nn.NeuralNetwork.Companion.map
import kotlin.math.exp
import kotlin.math.tanh
import kotlin.random.Random

class NeuronLayer(
    neurons: Int,
    inputsPerNeuron: Int,
    val activation: (Double) -> Double = sigmoid,
    val derivative: (Double) -> Double = sigmoidDerivative,
    var weights: Array<DoubleArray> = Array(inputsPerNeuron) { DoubleArray(neurons) { Random.nextDouble(-1.0, 1.0) } }
) {

    fun adjust(adjustments: Array<DoubleArray>) {
        weights.apply(adjustments, addition)
    }

    companion object {
        val sigmoid: (Double) -> Double = {
            1 / (1 + exp(-it))
        }
        val sigmoidDerivative: (Double) -> Double = {
            it * (1 - it)
        }
        val tanhActivation: (Double) -> Double = {
            tanh(it)
        }
        val tanhDerivative: (Double) -> Double = {
            1 - tanh(it) * tanh(it)
        }
    }
}