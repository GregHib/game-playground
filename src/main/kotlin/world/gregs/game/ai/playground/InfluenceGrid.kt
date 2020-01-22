package world.gregs.game.ai.playground

import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.*
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sqrt

typealias Distance = (x1: Int, y1: Int, x2: Int, y2: Int) -> Double

class InfluenceGrid {

    inner class GridPane(width: Int, height: Int) : JPanel(), MouseListener {
        val data = Array(width) { Array(height) { 0.0 } }

        override fun getPreferredSize(): Dimension {
            return Dimension(900, 900)
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            val g2d = g.create() as Graphics2D
            g.color = Color.BLACK
            g.fillRect(0, 0, width, height)
            val size = height / data[0].size
            //Fill
            for (x in data.indices) {
                for (y in data[0].indices) {
                    val value = data[x][y]
                    if (value > 0) {
                        val colour = Color(0, 0, 255, round(value * 255).toInt())
                        g.color = colour
                        g.fillRect(x * size + (width - size * data.size) / 2, y * size, size, size)
                    } else if (value < 0) {
                        val colour = Color(255, 0, 0, round((-value) * 255).toInt())
                        g.color = colour
                        g.fillRect(x * size + (width - size * data.size) / 2, y * size, size, size)
                    }
                }
            }
            //Outline
            g.color = Color.GRAY
            for (x in data.indices) {
                for (y in data[0].indices) {
                    g.drawRect(x * size + (width - size * data.size) / 2, y * size, 0, 0)
                }
            }
            g2d.dispose()
        }

        override fun mouseReleased(e: MouseEvent?) {
        }

        override fun mouseEntered(e: MouseEvent?) {
        }

        override fun mouseClicked(e: MouseEvent) {
            val size = height / data[0].size
            val gridX = (e.x - (width - size * data.size) / 2) / size
            val gridY = e.y / size
            if (SwingUtilities.isRightMouseButton(e)) {
                data.modify(gridX, gridY, maxDistance) { distance ->
                    -(1 - (distance / maxDistance))
                }
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                data.modify(gridX, gridY, maxDistance) { distance ->
                    1 - (distance / maxDistance)
                }
            }
            repaint()
        }

        override fun mouseExited(e: MouseEvent?) {
        }

        override fun mousePressed(e: MouseEvent?) {
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            InfluenceGrid()
        }
    }

    val maxDistance = 10.0

    val manhattan: Distance = { x1, y1, x2, y2 -> (abs(x1 - x2) + abs(y1 - y2)).toDouble() }//Diamond
    val chebyshev: Distance = { x1, y1, x2, y2 -> abs(x1 - x2).coerceAtLeast(abs(y1 - y2)).toDouble() }//Square
    val euclidean: Distance = { x1, y1, x2, y2 -> sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)).toDouble()) }//Circle

    val predicate: (x1: Int, y1: Int, x2: Int, y2: Int) -> Double = euclidean

    init {
        EventQueue.invokeLater {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (ex: ClassNotFoundException) {
                ex.printStackTrace()
            } catch (ex: InstantiationException) {
                ex.printStackTrace()
            } catch (ex: IllegalAccessException) {
                ex.printStackTrace()
            } catch (ex: UnsupportedLookAndFeelException) {
                ex.printStackTrace()
            }
            val frame = JFrame("Testing")
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            val pane = GridPane(50, 50)
            pane.addMouseListener(pane)
            frame.add(pane)
            frame.pack()
            frame.setLocationRelativeTo(null)
            frame.isVisible = true
        }
    }

    fun Array<Array<Double>>.modify(pointX: Int, pointY: Int, maxDistance: Double, modifier: (Double) -> Double) {
        for (x in indices) {
            for (y in this[0].indices) {
                val distance = predicate(x, y, pointX, pointY)
                if (distance < maxDistance) {
                    this[x][y] += modifier.invoke(distance)
                    if (this[x][y] > 1.0) {
                        this[x][y] = 1.0
                    }
                    if (this[x][y] < -1.0) {
                        this[x][y] = -1.0
                    }
                }
            }
        }
    }
}