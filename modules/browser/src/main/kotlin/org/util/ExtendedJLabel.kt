/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util

import java.awt.Cursor
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Rectangle
import javax.swing.JLabel
import javax.swing.Scrollable
import javax.swing.SwingUtilities
import javax.swing.UIManager

open class ExtendedJLabel(text: String) : JLabel(text), Scrollable, TextDisplay {

    var isUnderlined = false
        set(underlined) {
            field = underlined
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
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

