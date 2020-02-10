package world.gregs.game.playground.pathfinding.jps.js

import java.util.*


/**
 * Initializer; sets up variables, creates reference grid and actual grid, gets start and end points, initiates search
 *
 * @param xMax (int) maximum x value on map + 1 (if xMax==100, actual x maximum is 99)
 * @param yMax (int) maximum y value on map + 1 (if yMax==100, actual y maximum is 99)
 * @param xIsland (int) when using uniform map generation, how many islands on the x axis
 * @param yIsland (int) when using uniform map generation, how many islands on the y axis
 * @param uniform (boolean) if true then land created is a uniform checkered pattern (depending on xIsland, yIsland), if false then random land generated
 */
class JPS(internal var xMax: Int, internal var yMax: Int, internal var xIsland: Int, internal var yIsland: Int, uniform: Boolean, internal var draw: Boolean, preMadeGrid: Array<Array<JPSNode>>?) {
    internal var grid: Grid
    internal var startX: Int = 0
    internal var startY: Int = 0
    internal var endX: Int = 0
    internal var endY: Int = 0  //variables for reference grid
    internal var dxMax: Int = 0
    internal var dyMax: Int = 0
    internal var dstartX: Int = 0
    internal var dstartY: Int = 0
    internal var dendX: Int = 0
    internal var dendY: Int = 0       //variables for Large Nod
    internal var tmpXY: IntArray? = null
    internal var neighbors: Array<IntArray>? = null
    internal var ng: Float = 0.toFloat()
    internal var tmpNode: JPSNode? = null
    internal var cur: JPSNode? = null
    internal var successors: Array<JPSNode?>? = null
    internal var possibleSuccess: Array<JPSNode?>? = null
    internal var trail: ArrayList<JPSNode>? = null

    init {
        if (preMadeGrid == null) {
            grid = Grid(xMax, yMax, xIsland, yIsland, uniform)  //grid is created
        } else {
            grid = Grid(
                xMax,
                yMax,
                xIsland,
                yIsland,
                preMadeGrid
            )  //preMadeGrid is passed in because there CAN BE ONLY ONE GRID
        }
        val startPos = grid.openPos //startPos returns random {x,y} that does not lie on an obstacle
        this.startX = startPos[0]   //the start point x value
        this.startY = startPos[1]      //the start point y value
        val endPos = grid.openPos //startPos returns random {x,y} that does not lie on an obstacle
        this.endX = endPos[0]      //the end point x value
        this.endY = endPos[1]      //the end point y value
        val timeStart = System.currentTimeMillis()
        search()
        val timeEnd = System.currentTimeMillis()
        println("Time: " + (timeEnd - timeStart) + " ms")
    }//maximum x value on map + 1 (if xMax==100, actual x maximum is 99)
    //maximum y value on map + 1 (if yMax==100, actual y maximum is 99)
    //when using uniform map generation, how many islands on the x axis
    //when using uniform map generation, how many islands on the y axis

    /**
     * Orchestrates the Jump Point Search; it is explained further in comments below.
     */
    fun search() {
//        println("Jump Point Search\n----------------")
//        println("Start X: $startX Y: $startY")  //Start and End points are printed for reference
//        println("End   X: $endX Y: $endY")
        grid.getNode(startX, startY)!!.updateGHFP(0.0f, 0.0f, null)
        grid.heapAdd(grid.getNode(startX, startY)!!)  //Start node is added to the heap
        while (true) {
            cur = grid.heapPopNode()              //the current node is removed from the heap.
            if (draw) {
                grid.drawVisited(cur!!.x, cur!!.y)
            }  //draw current point
            if (cur!!.x == endX && cur!!.y == endY) {        //if the end node is found
//                println("Path Found!")  //print "Path Found!"
                if (draw) {
                    grid.drawStart(startX, startY)
                    grid.drawEnd(endX, endY)
                    grid.picPrint("2 - JumpPoints")
                } //draw start, end, and print the picture sans path
                trail = grid.pathCreate(cur!!)    //the path is then created
                if (draw) {
                    grid.picPrint("3 - PathAndPoints")
                }   //printed the picture with path
                break                //loop is done
            }
            possibleSuccess = identifySuccessors(cur!!)  //get all possible successors of the current node
            for (i in possibleSuccess!!.indices) {     //for each one of them
                if (possibleSuccess!![i] != null) {                //if it is not null
                    grid.heapAdd(possibleSuccess!![i]!!)        //add it to the heap for later use (a possible future cur)
                }
            }
            if (grid.heapSize() == 0) {                        //if the grid size is 0, and we have not found our end, the end is unreachable
//                println("No Path....")            //print "No Path...." to (lolSpark) notify user
                if (draw) {
                    grid.picPrint("3 - No Path")
                }        //print picture without path
                break                                        //loop is done
            }
        }
    }

    /**
     * returns all nodes jumped from given node
     *
     * @param node
     * @return all nodes jumped from given node
     */
    fun identifySuccessors(node: JPSNode): Array<JPSNode?> {
        successors = arrayOfNulls(8)                //empty successors list to be returned
        neighbors = getNeighborsPrune(node)    //all neighbors after pruned
        for (i in neighbors!!.indices) { //for each of these neighbors
            tmpXY = jump(neighbors!![i][0], neighbors!![i][1], node.x, node.y) //get next jump point
            if (tmpXY!![0] != -1) {                                //if that point is not null( {-1,-1} )
                val x = tmpXY!![0]
                val y = tmpXY!![1]
                ng = grid.toPointApprox(
                    x.toFloat(),
                    y.toFloat(),
                    node.x,
                    node.y
                ) + node.g   //get the distance from start
                if (grid.getNode(x, y)!!.f <= 0 || grid.getNode(
                        x,
                        y
                    )!!.g > ng
                ) {  //if this node is not already found, or we have a shorter distance from the current node
                    grid.getNode(x, y)!!.updateGHFP(
                        grid.toPointApprox(x.toFloat(), y.toFloat(), node.x, node.y) + node.g,
                        grid.toPointApprox(x.toFloat(), y.toFloat(), endX, endY),
                        node
                    ) //then update the rest of it
                    successors!![i] = grid.getNode(x, y)  //add this node to the successors list to be returned
                }
            }
        }
        return successors!!  //finally, successors is returned
    }

    /**
     * jump method recursively searches in the direction of parent (px,py) to child, the current node (x,y).
     * It will stop and return its current position in three situations:
     *
     * 1) The current node is the end node. (endX, endY)
     * 2) The current node is a forced neighbor.
     * 3) The current node is an intermediate step to a node that satisfies either 1) or 2)
     *
     * @param x (int) current node's x
     * @param y (int) current node's y
     * @param px (int) current.parent's x
     * @param py (int) current.parent's y
     * @return (int[]={x,y}) node which satisfies one of the conditions above, or null if no such node is found.
     */
    fun jump(x: Int, y: Int, px: Int, py: Int): IntArray {
        var jx = intArrayOf(-1, -1) //used to later check if full or null
        var jy = intArrayOf(-1, -1) //used to later check if full or null
        val dx = (x - px) / Math.max(
            Math.abs(x - px),
            1
        ) //because parents aren't always adjacent, this is used to find parent -> child direction (for x)
        val dy = (y - py) / Math.max(
            Math.abs(y - py),
            1
        ) //because parents aren't always adjacent, this is used to find parent -> child direction (for y)

        if (!grid.walkable(x, y)) { //if this space is not grid.walkable, return a null.
            return tmpInt(-1, -1) //in this system, returning a {-1,-1} equates to a null and is ignored.
        }
        if (x == this.endX && y == this.endY) {   //if end point, return that point. The search is over! Have a beer.
            return tmpInt(x, y)
        }
        if (dx != 0 && dy != 0) {  //if x and y both changed, we are on a diagonally adjacent square: here we check for forced neighbors on diagonals
            if (grid.walkable(x - dx, y + dy) && !grid.walkable(
                    x - dx,
                    y
                ) || //we are moving diagonally, we don't check the parent, or our next diagonal step, but the other diagonals
                grid.walkable(x + dx, y - dy) && !grid.walkable(x, y - dy)
            ) {  //if we find a forced neighbor here, we are on a jump point, and we return the current position
                return tmpInt(x, y)
            }
        } else { //check for horizontal/vertical
            if (dx != 0) { //moving along x
                if (grid.walkable(x + dx, y + 1) && !grid.walkable(x, y + 1) || //we are moving along the x axis
                    grid.walkable(x + dx, y - 1) && !grid.walkable(x, y - 1)
                ) {  //we check our side nodes to see if they are forced neighbors
                    return tmpInt(x, y)
                }
            } else {
                if (grid.walkable(x + 1, y + dy) && !grid.walkable(x + 1, y) ||  //we are moving along the y axis
                    grid.walkable(x - 1, y + dy) && !grid.walkable(x - 1, y)
                ) {     //we check our side nodes to see if they are forced neighbors
                    return tmpInt(x, y)
                }
            }
        }

        if (dx != 0 && dy != 0) { //when moving diagonally, must check for vertical/horizontal jump points
            jx = jump(x + dx, y, x, y)
            jy = jump(x, y + dy, x, y)
            if (jx[0] != -1 || jy[0] != -1) {
                return tmpInt(x, y)
            }
        }
        return if (grid.walkable(x + dx, y) || grid.walkable(
                x,
                y + dy
            )
        ) { //moving diagonally, must make sure one of the vertical/horizontal neighbors is open to allow the path
            jump(x + dx, y + dy, x, y)
        } else { //if we are trying to move diagonally but we are blocked by two touching corners of adjacent nodes, we return a null
            tmpInt(-1, -1)
        }
    }

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
     * Returns nodes that should be jumped based on the parent location in relation to the given node.
     *
     * @param node (Node) node which has a parent (not the start node)
     * @return (ArrayList<Node>) list of nodes that will be jumped
    </Node> */
    fun getNeighborsPrune(node: JPSNode): Array<IntArray> {
        val parent = node.parent    //the parent node is retrieved for x,y values
        val x = node.x
        val y = node.y
        val px: Int
        val py: Int
        val dx: Int
        val dy: Int
        val neighbors = Array(5) { IntArray(2) }
        //directed pruning: can ignore most neighbors, unless forced
        if (parent != null) {
            px = parent!!.x
            py = parent!!.y
            //get the normalized direction of travel
            dx = (x - px) / Math.max(Math.abs(x - px), 1)
            dy = (y - py) / Math.max(Math.abs(y - py), 1)
            //search diagonally
            if (dx != 0 && dy != 0) {
                if (grid.walkable(x, y + dy)) {
                    neighbors[0] = tmpInt(x, y + dy)
                }
                if (grid.walkable(x + dx, y)) {
                    neighbors[1] = tmpInt(x + dx, y)
                }
                if (grid.walkable(x, y + dy) || grid.walkable(x + dx, y)) {
                    neighbors[2] = tmpInt(x + dx, y + dy)
                }
                if (!grid.walkable(x - dx, y) && grid.walkable(x, y + dy)) {
                    neighbors[3] = tmpInt(x - dx, y + dy)
                }
                if (!grid.walkable(x, y - dy) && grid.walkable(x + dx, y)) {
                    neighbors[4] = tmpInt(x + dx, y - dy)
                }
            } else {
                if (dx == 0) {
                    if (grid.walkable(x, y + dy)) {
                        if (grid.walkable(x, y + dy)) {
                            neighbors[0] = tmpInt(x, y + dy)
                        }
                        if (!grid.walkable(x + 1, y)) {
                            neighbors[1] = tmpInt(x + 1, y + dy)
                        }
                        if (!grid.walkable(x - 1, y)) {
                            neighbors[2] = tmpInt(x - 1, y + dy)
                        }
                    }
                } else {
                    if (grid.walkable(x + dx, y)) {
                        if (grid.walkable(x + dx, y)) {
                            neighbors[0] = tmpInt(x + dx, y)
                        }
                        if (!grid.walkable(x, y + 1)) {
                            neighbors[1] = tmpInt(x + dx, y + 1)
                        }
                        if (!grid.walkable(x, y - 1)) {
                            neighbors[2] = tmpInt(x + dx, y - 1)
                        }
                    }
                }
            }
        } else {//return all neighbors
            return grid.getNeighbors(node) //adds initial nodes to be jumped from!
        }

        return neighbors //this returns the neighbors, you know
    }
}