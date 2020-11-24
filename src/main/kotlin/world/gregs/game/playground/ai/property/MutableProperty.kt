package world.gregs.game.playground.ai.property

abstract class MutableProperty<T : Any>(initialValue: T) : Property<T>(initialValue) {

    fun set(value: T) {
        this.value = value
    }

}