package world.gregs.game.playground.spatial

import org.junit.Test
import java.awt.Point

internal class CentroidTest {

    @Test
    fun `Find centroid`() {
        //DAO (dragon age origins)
        val grid = arrayOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 1),
            intArrayOf(0, 0, 0)
        )

        val points = mutableListOf<Point>()
        for(x in grid.indices) {
            for(y in grid[0].indices) {
                if(grid[x][y] == 0)
                points.add(Point(x, y))
            }
        }

        val point = centroid(points.toTypedArray())
        println(point)
    }

    fun centroid(knots: Array<Point>): Point? {
        var centroidX = 0.0
        var centroidY = 0.0
        for (knot in knots) {
            centroidX += knot.getX()
            centroidY += knot.getY()
        }
        return Point((centroidX / knots.size).toInt(), (centroidY / knots.size).toInt())
    }


}