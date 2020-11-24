package world.gregs.game.playground.ai.property

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A generic observable property with multiple observers.
 */
abstract class Observable<T : Any>(initialValue: T) : ReadWriteProperty<Any?, T> {
    protected var value = initialValue
        set(value) {
            if(field != value) {
                listeners.forEach { listener ->
                    listener.invoke(value)
                }
            }
            field = value
        }
    private val listeners = mutableListOf<(T) -> Unit>()

    fun addListener(listener: (T) -> Unit) = listeners.add(listener)

    fun removeListener(listener: (T) -> Unit) = listeners.remove(listener)

    fun clearListeners() = listeners.clear()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}