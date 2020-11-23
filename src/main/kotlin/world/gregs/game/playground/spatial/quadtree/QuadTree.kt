package world.gregs.game.playground.spatial.quadtree

import java.awt.Point
import java.awt.Rectangle

interface QuadTree {
    /**
     * The capacity of a leaf before division
     */
    val capacity: Int

    /**
     * Inserts a point into the tree
     */
    fun insert(point: Point): Boolean

    /**
     * Queries an [area] for points
     */
    fun query(area: Rectangle, results: MutableList<Point>): List<Point>

}