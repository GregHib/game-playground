package world.gregs.game.playground.pathfinding.jps.js

import java.util.*


class Heap {
    internal var list = LinkedList<FloatArray>()
    lateinit var listit: Iterator<FloatArray>

    val size: Int
        get() = list.size

    fun add(newXY: FloatArray) {
        if (list.size > 0) {
            listit = list.iterator()
            var tmp: FloatArray
            var count = 0
            while (true) {
                tmp = listit.next()
                if (tmp[2] > newXY[2]) {
                    list.add(count, newXY)
                    break
                } else {
                    count++
                }
                if (!listit.hasNext()) {
                    list.add(count, newXY)
                    break
                }
            }
        } else {
            list.add(newXY)
        }
    }

    fun pop(): FloatArray? {
        try {
            return list.pop()
        } catch (e: Exception) {
            println("List is Empty!!")
            return null
        }

    }

    fun printHeap() {
        listit = list.iterator()
        var tmp = listit.next()
        var flag = true
        while (flag) {
            println("Node f: " + tmp[2])
            if (listit.hasNext()) {
                tmp = listit.next()
            } else {
                flag = false
            }
        }
        println("------")
    }
}