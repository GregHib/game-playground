package world.gregs.game.playground.pathfinding.bfs

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*
import kotlin.system.measureNanoTime

object Bench {
    @JvmStatic
    fun main(args: Array<String>) {

        val target = object : TargetPredicate {
            override val x: Int = -1
            override val y: Int = -1

            override fun reached(x: Int, y: Int, z: Int) = false

            override fun distance(x: Int, y: Int, z: Int) = -1
        }

        val size = 128
        val data = File("./benchmarks/src/jmh/resources/test2.dat").readBytes()
        val collisionData = BitSet(size * size)
        for (x in 0 until size) {
            for (y in 0 until size) {
                collisionData[UnsafeBreadthFirstSearch.hash(x, y)] = data[x + (y * size)] == 1.toByte()
            }
        }
        val collisions = object : Collisions {
            override fun blocked(fromX: Int, fromY: Int, toX: Int, toY: Int, direction: Int, z: Int): Boolean {
                return collisionData[UnsafeBreadthFirstSearch.hash(toX, toY)]
            }

            override fun blocked(from: Int, to: Int, direction: Int, z: Int): Boolean {
                return collisionData[to]
            }
        }
        val bfs = UnsafeBreadthFirstSearch(collisions, size, size)

        fun avg(times: Int, block: () -> Unit): Long {
            var total = 0L
            repeat(times) {
                total += measureNanoTime {
                    block.invoke()
                }
            }
            return total / times
        }

        fun total(times: Int, block: () -> Unit): Long {
            var total = 0L
            repeat(times) {
                total += measureNanoTime {
                    block.invoke()
                }
            }
            return total
        }

        bfs.search(target, 64, 64, -1)

        runBlocking {
            repeat(20) {
                delay(it * 250L)
                repeat(it) {
                    println("${measureNanoTime { bfs.search(target, 64, 64, -1) }}ns")
                }
            }
        }
//        println("${avg(1) { bfs.search(target, 64, 64, -1) }}ns")
//        println("${avg(1) { bfs.searchInside(target, 64, 64, -1) }}ns inside")
//
//        println("${avg(10) { bfs.search(target, 64, 64, -1) }}ns")
//        println("${avg(10) { bfs.searchInside(target, 64, 64, -1) }}ns inside")
//
//        println("${avg(100) { bfs.search(target, 64, 64, -1) }}ns")
//        println("${avg(100) { bfs.searchInside(target, 64, 64, -1) }}ns inside")

//        println("${total(2000) { bfs.search(target, 64, 64, -1) }}ns")
        println("${total(2000) { bfs.searchInside(target, 64, 64, -1) }}ns inside")
    }
}
