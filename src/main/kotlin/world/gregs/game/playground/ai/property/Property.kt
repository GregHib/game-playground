package world.gregs.game.playground.ai.property

/**
 * A delegated value which binds itself to other self created properties
 */
abstract class Property<T : Any> protected constructor(initialValue: T) : Observable<T>(initialValue) {

    fun get(): T = value

    override fun toString(): String {
        return value.toString()
    }

}