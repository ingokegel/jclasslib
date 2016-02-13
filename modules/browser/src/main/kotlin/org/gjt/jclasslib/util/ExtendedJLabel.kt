/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util

import javax.swing.*
import java.awt.*

open class ExtendedJLabel(text: String) : JLabel(text), Scrollable, TextDisplay {

    var isUnderlined = false
        set(underlined) {
            field = underlined
            repaint()
        }
    var autoTooltip = false
        set(autoTooltip) {
            field = autoTooltip
            if (autoTooltip) {
                toolTipText = text
            }
        }

    override fun getPreferredScrollableViewportSize(): Dimension = size
    override fun getScrollableBlockIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int = width / 10
    override fun getScrollableTracksViewportWidth(): Boolean = false
    override fun getScrollableTracksViewportHeight(): Boolean = false
    override fun getScrollableUnitIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int = 10

    override fun setText(text: String) {
        super.setText(text)
        if (autoTooltip) {
            toolTipText = text
        }
    }

    fun setText(number: Short) {
        text = number.toString()
    }

    fun setText(number: Int) {
        text = number.toString()
    }

    fun setText(number: Double) {
        text = number.toString()
    }

    fun setText(number: Float) {
        text = number.toString()
    }

    fun setText(number: Long) {
        text = number.toString()
    }

    override fun paint(g: Graphics) {
        super.paint(g)

        if (isUnderlined) {
            val i = insets
            val fm = g.fontMetrics

            val textRect = Rectangle()
            val viewRect = Rectangle(i.left, i.top, width - (i.right + i.left), height - (i.bottom + i.top))

            SwingUtilities.layoutCompoundLabel(
                    this,
                    fm,
                    text,
                    icon,
                    verticalAlignment,
                    horizontalAlignment,
                    verticalTextPosition,
                    horizontalTextPosition,
                    viewRect,
                    Rectangle(),
                    textRect,
                    if (text == null) 0 else UIManager.getInt("Button.textIconGap")
            )

            val yOffset = if (UIManager.getLookAndFeel().isNativeLookAndFeel && System.getProperty("os.name").startsWith("Windows")) 1 else 2
            g.fillRect(textRect.x + UIManager.get("Button.textShiftOffset") as Int,
                    textRect.y + fm.ascent + UIManager.get("Button.textShiftOffset") as Int + yOffset,
                    textRect.width,
                    1
            )
        }
    }
}

