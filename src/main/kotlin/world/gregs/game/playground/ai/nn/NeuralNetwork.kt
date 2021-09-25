package world.gregs.game.playground.ai.nn

class NeuralNetwork(
    private val layer1: NeuronLayer,
    private val layer2: NeuronLayer,
    private val learningRate: Double = 0.1
) {
    private lateinit var outputLayer1: Array<DoubleArray>
    lateinit var outputLayer2: Array<DoubleArray>

    fun input(inputs: Array<DoubleArray>) {
        outputLayer1 = inputs.combine(layer1.weights).mapIndexed { _, _, value -> layer1.activation(value) }
        outputLayer2 = outputLayer1.combine(layer2.weights).mapIndexed { _, _, value -> layer2.activation(value) }
    }

    fun train(inputs: Array<DoubleArray>, outputs: Array<DoubleArray>, iterations: Int) {
        repeat(iterations) {
            input(inputs)

            val errorLayer2 = outputs.map(outputLayer2, subtract)
            errorLayer2.apply(outputLayer2.map(layer2.derivative), multiply)

            val errorLayer1 = errorLayer2.combine(layer2.weights.transpose())
            errorLayer1.apply(outputLayer1.map(layer1.derivative), multiply)

            val adjustmentLayer1 = inputs.transpose().combine(errorLayer1)
            layer1.adjust(adjustmentLayer1.mapIndexed { _, _, value -> value * learningRate })

            val adjustmentLayer2 = outputLayer1.transpose().combine(errorLayer2)
            layer2.adjust(adjustmentLayer2.mapIndexed { _, _, value -> value * learningRate })

            if (it % 10000 == 0) {
                println(" Training iteration $it of $iterations")
            }
        }
    }

    companion object {

        val addition: (Double, Double) -> Double = { a, b -> a + b }
        val subtract: (Double, Double) -> Double = { a, b -> a - b }
        val multiply: (Double, Double) -> Double = { a, b -> a * b }
        fun Array<DoubleArray>.apply(other: Array<DoubleArray>, block: (Double, Double) -> Double) = apply { x, y, value -> block(value, other[x][y]) }
        fun Array<DoubleArray>.apply(block: (Double) -> Double) = apply { _, _, value -> block(value) }
        fun Array<DoubleArray>.apply(block: (Int, Int, Double) -> Double) {
            for (x in indices) {
                for (y in this[x].indices) {
                    this[x][y] = block(x, y, this[x][y])
                }
            }
        }

        fun Array<DoubleArray>.map(block: (Double) -> Double) = mapIndexed { _, _, value -> block(value) }
        fun Array<DoubleArray>.map(other: Array<DoubleArray>, block: (Double, Double) -> Double) = mapIndexed { x, y, value -> block(value, other[x][y]) }
        fun Array<DoubleArray>.mapIndexed(block: (Int, Int, Double) -> Double) = mapIndexed { x, values -> values.mapIndexed { y, value -> block(x, y, value) }.toDoubleArray() }.toTypedArray()
        fun Array<DoubleArray>.transpose(): Array<DoubleArray> = Array(this[0].size) { x -> DoubleArray(this.size) { y -> this[y][x] } }
        fun Array<DoubleArray>.combine(other: Array<DoubleArray>): Array<DoubleArray> {
            return Array(size) { x ->
                DoubleArray(other[0].size) { y ->
                    var sum = 0.0
                    repeat(this[0].size) { i ->
                        sum += this[x][i] * other[i][y]
                    }
                    sum
                }
            }
        }
    }
}