package world.gregs.game.playground.pathfinding.jps.js

import java.util.*


class Grid {
    private var grid: Array<Array<JPSNode>>? = null
    private var uniform: Boolean = false
    private var xIsland: Int = 0
    private var yIsland: Int = 0
    private var xMax: Int = 0
    private var yMax: Int = 0
    private var xMin: Int = 0
    private var yMin: Int = 0
    private val trailCol = intArrayOf(255, 255, 0)
    private val expandedCol = intArrayOf(0, 0, 0)
    private val visitedCol = intArrayOf(255, 255, 255)
    private val landCol = intArrayOf(10, 100, 10)
    private val waterCol = intArrayOf(0, 0, 255)
    private val startCol = intArrayOf(0, 255, 0)
    private val endCol = intArrayOf(255, 0, 0)
//    private var map: PixelDraw? = null
    private var heap: Heap? = null

    /**
     * Finds an open spot. Used for finding random start/end points.
     *
     * @return int[] open spot
     */
    //gets random x
    //gets random y
    //if this (x,y) pair is walkable (not an obstacle and on the map)
    //combine the approved x and y
    //return this pair!
    val openPos: IntArray
        get() {
            val rand = Random()
            while (true) {
                val tA = rand.nextInt(xMax)
                val tB = rand.nextInt(yMax)
                if (walkable(tA, tB)) {
                    return intArrayOf(tA, tB)
                }
            }
        }

    /**
     * Grid is created, Land is generated in either uniform or random fashion, landscape 'Map' is created in printed.
     *
     *
     * @param xMax - (int) maximum x coordinate
     * @param yMax - (int) maximum y coordinate
     * @param xIsland (int) number of islands along x axis
     * @param yIsland (int) number of islands along y axis
     * @param uniform (boolean) if true then land is generated in a uniform fashion, if false then land is randomly generated
     */
    constructor(xMax: Int, yMax: Int, xIsland: Int, yIsland: Int, uniform: Boolean) {
        this.xMax = xMax
        this.yMax = yMax
        this.xIsland = xIsland
        this.yIsland = yIsland
        this.yMin = 0
        this.xMin = this.yMin
        this.uniform = uniform
//        map = PixelDraw(this.xMax, this.yMax)
        grid = Array<Array<JPSNode>>(this.xMax) { x -> Array(this.yMax) { y -> JPSNode(x, y)} }
        if (uniform) {
            uniformLandGenerator()
        } else {
            randomLandGenerator(xMax / 20, xMax / 6)
        }
//        map!!.picPrint("1 - Map")
        heap = Heap()
    }

    /**
     * This is the constuctor used for comparison. It can be passed an entire Node[][] grid.
     *
     *
     * @param xMax - (int) maximum x coordinate
     * @param yMax - (int) maximum y coordinate
     * @param xIsland (int) number of islands along x axis
     * @param yIsland (int) number of islands along y axis
     * @param grid (Node[][]) an entire grid is passed through for comparison
     */
    constructor(xMax: Int, yMax: Int, xIsland: Int, yIsland: Int, grid: Array<Array<JPSNode>>) {
        this.xMax = xMax
        this.yMax = yMax
        this.xIsland = xIsland
        this.yIsland = yIsland
        this.yMin = 0
        this.xMin = this.yMin
//        map = PixelDraw(this.xMax, this.yMax)
        this.grid = grid
//        map!!.picPrint("1 - Map")
        heap = Heap()
    }


    /**
     * returns all adjacent nodes that can be traversed
     *
     * @param node (Node) finds the neighbors of this node
     * @return (int[][]) list of neighbors that can be traversed
     */
    fun getNeighbors(node: JPSNode): Array<IntArray> {
        val neighbors = Array(8) { IntArray(2) }
        val x = node.x
        val y = node.y
        var d0 = false //These booleans are for speeding up the adding of nodes.
        var d1 = false
        var d2 = false
        var d3 = false

        if (walkable(x, y - 1)) {
            neighbors[0] = tmpInt(x, y - 1)
            d1 = true
            d0 = d1
        }
        if (walkable(x + 1, y)) {
            neighbors[1] = tmpInt(x + 1, y)
            d2 = true
            d1 = d2
        }
        if (walkable(x, y + 1)) {
            neighbors[2] = tmpInt(x, y + 1)
            d3 = true
            d2 = d3
        }
        if (walkable(x - 1, y)) {
            neighbors[3] = tmpInt(x - 1, y)
            d0 = true
            d3 = d0
        }
        if (d0 && walkable(x - 1, y - 1)) {
            neighbors[4] = tmpInt(x - 1, y - 1)
        }
        if (d1 && walkable(x + 1, y - 1)) {
            neighbors[5] = tmpInt(x + 1, y - 1)
        }
        if (d2 && walkable(x + 1, y + 1)) {
            neighbors[6] = tmpInt(x + 1, y + 1)
        }
        if (d3 && walkable(x - 1, y + 1)) {
            neighbors[7] = tmpInt(x - 1, y + 1)
        }
        return neighbors
    }

    //---------------------------Passability------------------------------//
    /**
     * Tests an x,y node's passability
     *
     * @param x (int) node's x coordinate
     * @param y (int) node's y coordinate
     * @return (boolean) true if the node is obstacle free and on the map, false otherwise
     */
    fun walkable(x: Int, y: Int): Boolean {
        return if (uniform) {
            (x < xMax && y < yMax         //smaller than max

                    && x >= xMin && y >= yMin       //larger than min

                    && Math.sin(Math.PI + xIsland.toDouble() * 2.0 * Math.PI * x.toDouble() / 1000.0) + Math.cos(Math.PI / 2.0 + yIsland.toDouble() * 2.0 * Math.PI * y.toDouble() / 1000.0) > -.1)
        } else {    //for randomized land generation, all nodes always contain correct "pass" boolean
            try {
                getNode(x, y)!!.pass
            } catch (e: Exception) {
                false
            }

        }
    }
    //--------------------------------------------------------------------//

    //---------------------------MAP DRAWING------------------------------//
    /**
     * Draws visited pixel to the map
     *
     * @param x (int) point to be drawn's x coordinate
     * @param y (int) point to be drawn's y coordinate
     */
    fun drawVisited(x: Int, y: Int) {
//        map!!.drawPixel(x, y, visitedCol)
    }

    /**
     * Draws expanded pixel to the map
     *
     * @param x (int) point to be drawn's x coordinate
     * @param y (int) point to be drawn's y coordinate
     */
    fun drawExpanded(x: Int, y: Int) {
//        map!!.drawPixel(x, y, expandedCol)
    }

    /**
     * Saves the picture to a png file in the folder of the program
     *
     * @param name (String) the file will be called 'name'
     */
    fun picPrint(name: String) {
//        map!!.picPrint(name)
    }

    /**
     * Draws a line from point (x,y) to point (px,py). The line is a nice mellow yellow.
     *
     * @param x (int) start point's x coordinate
     * @param y (int) start point's y coordinate
     * @param px (int) end point's x coordinate
     * @param py (int) end point's y coordinate
     */
    fun drawLine(x: Int, y: Int, px: Int, py: Int) {
//        map!!.drawLine(x, y, px, py, trailCol)
    }

    /**
     * Draws a start point at (x,y)
     * @param x (int) start point's x coordinate
     * @param y (int) start point's y coordinate
     */
    fun drawStart(x: Int, y: Int) {
//        map!!.drawPOI(x, y, startCol)
    }

    /**
     * Draws an end point at (x,y)
     * @param x (int) end point's x coordinate
     * @param y (int) end point's y coordinate
     */
    fun drawEnd(x: Int, y: Int) {
//        map!!.drawPOI(x, y, endCol)
    }

    fun pathCreate(node: JPSNode): ArrayList<JPSNode> {
        var node = node
        val trail = ArrayList<JPSNode>()
//        println("Tracing Back Path...")
        while (node.parent != null) {
            try {
                trail.add(0, node)
            } catch (e: Exception) {
            }

            drawLine(node.parent!!.x, node.parent!!.y, node.x, node.y)
            node = node.parent!!
        }
//        println("Path Trace Complete!")
        return trail
    }
    //-----------------------------------------------------------------//

    //--------------------------HEAP-----------------------------------//
    /**
     * Adds a node's (x,y,f) to the heap. The heap is sorted by 'f'.
     *
     * @param node (Node) node to be added to the heap
     */
    fun heapAdd(node: JPSNode) {
        val tmp = floatArrayOf(node.x.toFloat(), node.y.toFloat(), node.f)
        heap!!.add(tmp)
    }

    /**
     * @return (int) size of the heap
     */
    fun heapSize(): Int {
        return heap!!.size
    }

    /**
     * @return (Node) takes data from popped float[] and returns the correct node
     */
    fun heapPopNode(): JPSNode? {
        val tmp = heap!!.pop() ?: return null
        return getNode(tmp[0].toInt(), tmp[1].toInt())
    }
    //-----------------------------------------------------------------//

    //-----------------------LAND GENERATION---------------------------//
    /**
     * Generates land based on a formula. Land forms like a checkered pattern.
     */
    fun uniformLandGenerator() {
        for (i in 0 until this.xMax) {
            for (j in 0 until this.yMax) {
                grid!![i][j] = JPSNode(i, j)
                if (grid!![i][j]!!.setPass(walkable(i, j))) {
                    grid!![i][j]!!.walkable = false
                } else {
                    grid!![i][j]!!.walkable = true
                }
            }
        }
    }

    /**
     * Generates land based on random factors. Land forms like an ugly hanging gardens.
     *
     * @param amount (int) number of islands to produce
     * @param size (int) general size of islands (random size is directly correlated to this number).
     */
    fun randomLandGenerator(amount: Int, size: Int) {
        for (i in 0 until this.xMax) {
            for (j in 0 until this.yMax) {
                grid!![i][j] = JPSNode(i, j)
                grid!![i][j]!!.f = -1f
//                map!!.drawPixel(i, j, waterCol)
            }
        }
        val rand = Random()
        var centerX: Int
        var centerY: Int
        var num1: Int
        var num2: Int
        for (i in 0 until amount) {
            centerX = rand.nextInt(xMax)
            centerY = rand.nextInt(yMax)
            num1 = rand.nextInt(size)
            for (j in 0 until num1) {
                num2 = rand.nextInt(size)
                for (k in 0 until num2) {
                    try {
                        grid!![centerX + j][centerY + k]?.pass = false
//                        map!!.drawPixel(centerX + j, centerY + k, landCol)
                    } catch (e: Exception) {
                    }

                }
            }
        }
    }
    //---------------------------------------------------------//
    /**
     * Encapsulates x,y in an int[] for returning. A helper method for the jump method
     *
     * @param x (int) point's x coordinate
     * @param y (int) point's y coordinate
     * @return ([]int) bundled x,y
     */
    fun tmpInt(x: Int, y: Int): IntArray {
        return intArrayOf(x, y)         //return it
    }

    /**
     * getFunc - Node at given x, y
     *
     * @param x (int) desired node x coordinate
     * @param y (int) desired node y coordinate
     * @return (Node) desired node
     */
    fun getNode(x: Int, y: Int): JPSNode? {
        try {
            return grid!![x][y]
        } catch (e: Exception) {
            return null
        }

    }

    fun toPointApprox(x: Float, y: Float, tx: Int, ty: Int): Float {
        return Math.sqrt(Math.pow(Math.abs(x - tx).toDouble(), 2.0) + Math.pow(Math.abs(y - ty).toDouble(), 2.0))
            .toFloat()
    }
}
