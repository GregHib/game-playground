package world.gregs.game.playground.ai.dist

class ProbabilityList : WeightedList() {

    var total = 0L
        private set

    override fun add(value: Any, weight: Long) {
        total += weight
        super.add(value, weight)
    }

    override fun remove(value: Any) {
        total -= map[value] ?: 0
        super.remove(value)
    }

    /**
     * Selects a value with a probability proportional to the nodes weight
     * https://en.wikipedia.org/wiki/Probability_distribution#Discrete_probability_distribution
     */
    override fun sample(): Any? {
        var total = total
        if (total == 0L) {
            total = -1
        }
        val r = total * Math.random()
        var sum = 0L
        val value = map.entries.firstOrNull {
            sum += it.value
            sum > r
        }?.key
        return if(value is WeightedList) value.sample() else value
    }

    override fun iterator(): Iterator<Any> {
        return map.iterator()
    }

}