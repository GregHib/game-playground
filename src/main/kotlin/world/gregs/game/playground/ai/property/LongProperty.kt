package world.gregs.game.playground.ai.property

class LongProperty(initialValue: Long) : MutableProperty<Long>(initialValue) {

    operator fun plus(value: Long) = transform { it + value }
    operator fun minus(value: Long) = transform { it - value }
    operator fun times(value: Long) = transform { it * value }
    operator fun div(value: Long) = transform { it / value }
    operator fun rem(value: Long) = transform { it % value }

    operator fun plus(util: Property<Long>) = combine(util) { a, b -> a + b }
    operator fun minus(util: Property<Long>) = combine(util) { a, b -> a - b }
    operator fun times(util: Property<Long>) = combine(util) { a, b -> a * b }
    operator fun div(util: Property<Long>) = combine(util) { a, b -> a / b }
    operator fun rem(util: Property<Long>) = combine(util) { a, b -> a % b }

    fun coerceIn(min: Long, max: Long) = transform { it.coerceIn(min, max) }

    /**
     * Creates a new property with the mutation applied that will update when this properties value changes.
     * @return another immutable property
     */
    fun transform(transform: (Long) -> Long): LongProperty {
        val utility = LongProperty(transform(value))
        addListener { value ->
            utility.value = transform.invoke(value)
        }
        return utility
    }

    /**
     * Combine two properties into one, updating the later when either of the former changes
     * @return another immutable property
     */
    fun <A : Any> combine(other: Property<A>, transform: (Long, A) -> Long): LongProperty {
        val utility = LongProperty(transform(value, other.get()))
        addListener { value ->
            utility.value = transform.invoke(value, other.get())
        }
        other.addListener { value ->
            utility.value = transform.invoke(this.value, value)
        }
        return utility
    }

    operator fun compareTo(value: Long): Int {
        return this.value.compareTo(value)
    }

    operator fun compareTo(utility: Property<Long>): Int {
        return value.compareTo(utility.get())
    }

}