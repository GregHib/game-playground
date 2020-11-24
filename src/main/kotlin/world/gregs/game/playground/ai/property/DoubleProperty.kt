package world.gregs.game.playground.ai.property

class DoubleProperty(initialValue: Double) : MutableProperty<Double>(initialValue) {

    operator fun plus(value: Double) = transform { it + value }
    operator fun minus(value: Double) = transform { it - value }
    operator fun times(value: Double) = transform { it * value }
    operator fun div(value: Double) = transform { it / value }
    operator fun rem(value: Double) = transform { it % value }

    operator fun plus(util: Property<Double>) = combine(util) { a, b -> a + b }
    operator fun minus(util: Property<Double>) = combine(util) { a, b -> a - b }
    operator fun times(util: Property<Double>) = combine(util) { a, b -> a * b }
    operator fun div(util: Property<Double>) = combine(util) { a, b -> a / b }
    operator fun rem(util: Property<Double>) = combine(util) { a, b -> a % b }

    fun coerceIn(min: Double, max: Double) = transform { it.coerceIn(min, max) }

    /**
     * Normalizes value to range [min] [max]
     */
    fun rescale(min: Double, max: Double) = transform { (it.coerceIn(min, max) - min) / (max - min) }

    /**
     * Creates a new property with the mutation applied that will update when this properties value changes.
     * @return another immutable property
     */
    fun transform(transform: (Double) -> Double): DoubleProperty {
        val utility = DoubleProperty(transform(value))
        addListener { value ->
            utility.value = transform.invoke(value)
        }
        return utility
    }

    /**
     * Combine two properties into one, updating the later when either of the former changes
     * @return another immutable property
     */
    fun <A : Any> combine(other: Property<A>, transform: (Double, A) -> Double): DoubleProperty {
        val utility = DoubleProperty(transform(value, other.get()))
        addListener { value ->
            utility.value = transform.invoke(value, other.get())
        }
        other.addListener { value ->
            utility.value = transform.invoke(this.value, value)
        }
        return utility
    }

    operator fun compareTo(value: Double): Int {
        return this.value.compareTo(value)
    }

    operator fun compareTo(utility: Property<Double>): Int {
        return value.compareTo(utility.get())
    }

}