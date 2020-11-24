package world.gregs.game.playground.ai.property

class BooleanProperty(initialValue: Boolean) : MutableProperty<Boolean>(initialValue) {

    operator fun compareTo(value: Boolean): Int {
        return this.value.compareTo(value)
    }

    operator fun compareTo(utility: Property<Boolean>): Int {
        return value.compareTo(utility.get())
    }

}