package world.gregs.game.playground

import java.awt.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException
import kotlin.math.max
import kotlin.random.Random

class SwingGrid {
    inner class GridPane(val data: Array<Array<Color?>>) : JPanel() {
        override fun getPreferredSize(): Dimension {
            return Dimension(900, 900)
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            val g2d = g.create() as Graphics2D

            val size = max(1, height/data[0].size)
            //Fill
            for(x in data.indices) {
                for(y in data[0].indices) {
                    if(data[x][y] != null) {
                        g.color = data[x][y]
                        g.fillRect(x * size + (width - size * data.size) / 2, y * size, size, size)
                    }
                }
            }
            g2d.dispose()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SwingGrid()
        }
    }

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
            val grid = Array(800) { Array<Color?>(800) { null } }
            for(x in grid.indices) {
                for(y in grid[0].indices) {
                    if(Random.nextDouble() < 0.15) {
                        grid[x][y] = Color.BLACK
                    }
                }
            }
            grid[5][5] = Color.BLUE
            grid[6][5] = Color.BLUE
            frame.add(GridPane(grid))
            frame.pack()
            frame.setLocationRelativeTo(null)
            frame.isVisible = true
        }
    }
}