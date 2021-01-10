package world.gregs.game.playground.pathfinding.bfs.unsafe

import sun.misc.Unsafe
import java.lang.reflect.Field

object Memory {
    fun getUnsafe(): Unsafe {
        val f: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
        f.isAccessible = true
        return f.get(null) as Unsafe
    }
}