package org.gjt.jclasslib.util

import java.awt.*
import javax.swing.*
import javax.swing.plaf.basic.BasicButtonUI

class ClosableTabComponent(private val tabbedPane: JTabbedPane) : JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)) {

    init {
        isOpaque = false
        border = BorderFactory.createEmptyBorder(2, 0, 0, 0)
        add(object : JLabel() {
            init {
                border = BorderFactory.createEmptyBorder(0, 0, 0, 5)
            }
            override fun getText(): String? {
                val index = tabbedPane.indexOfTabComponent(this@ClosableTabComponent)
                if (index != -1) {
                    return tabbedPane.getTitleAt(index)
                } else {
                    return null
                }
            }
        })
        add(CloseButton())
        val originalForeground = foreground
        if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
            tabbedPane.addChangeListener {
                val selectedIndex = tabbedPane.selectedIndex
                if (selectedIndex >= 0) {
                    val selected = tabbedPane.getTabComponentAt(selectedIndex) == this
                    components.forEach {
                        it.foreground = if (selected) Color.WHITE else originalForeground
                    }
                }
            }
        }
    }

    private inner class CloseButton : JButton() {

        private var inactiveColor: Color = INACTIVE_DARK_COLOR

        init {
            preferredSize = BUTTON_SIZE
            isContentAreaFilled = false
            isFocusable = false
            border = null
            isBorderPainted = false
            isRolloverEnabled = true
            setUI(BasicButtonUI())
            addActionListener {
                val index = tabbedPane.indexOfTabComponent(this@ClosableTabComponent)
                if (index != -1) {
                    tabbedPane.remove(index)
                }
            }
        }

        override fun updateUI() {
        }

        override fun setForeground(color: Color?) {
            super.setForeground(color)
            inactiveColor = if (foreground == Color.WHITE) {
                INACTIVE_LIGHT_COLOR
            } else {
                INACTIVE_DARK_COLOR
            }
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            (g.create() as Graphics2D).apply {
                stroke = STROKE
                color = if (getModel().isRollover) foreground else inactiveColor
                drawLine(OFFSET, OFFSET, width - OFFSET - 1, height - OFFSET - 1)
                drawLine(width - OFFSET - 1, OFFSET, OFFSET, height - OFFSET - 1)
            }
        }
    }

    companion object {
        private val BUTTON_SIZE = Dimension(17, 17)
        private val STROKE = BasicStroke(1.5.toFloat())
        private val OFFSET = 5
        private val INACTIVE_DARK_COLOR = Color(100, 100, 100)
        private val INACTIVE_LIGHT_COLOR = Color(230, 230, 230)
    }

}