package world.gregs.game.playground.ai.dist

open class WeightedList : Iterable<Any> {

    protected val map = mutableMapOf<Any, Long>()

    open fun add(value: Any, weight: Long) {
        map[value] = weight
    }

    open fun remove(value: Any) {
        map.remove(value)
    }

    open fun sample(): Any? {
        val value = map.maxByOrNull { it.value }?.key
        return if(value is WeightedList) value.sample() else value
    }

    override fun iterator(): Iterator<Any> {
        return map.keys.iterator()
    }
}