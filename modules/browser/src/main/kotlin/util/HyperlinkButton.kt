/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */
package org.gjt.jclasslib.util

import java.awt.*
import javax.swing.AbstractButton
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.border.Border
import javax.swing.border.EmptyBorder
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicButtonUI

class HyperlinkButton : JButton() {

    init {
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        isBorderPainted = false
        isContentAreaFilled = false
        isRolloverEnabled = true
        isOpaque = false
    }

    override fun updateUI() {
        setUI(BasicLinkButtonUI.createUI(this))
    }

    override fun getUIClassID(): String {
        return "LinkButtonUI"
    }

    @Suppress("ACCIDENTAL_OVERRIDE")
    private class BasicLinkButtonUI : BasicButtonUI() {

        override fun installDefaults(button: AbstractButton) {
            super.installDefaults(button)
            button.border = BORDER
        }

        override fun paintText(graphics: Graphics, c: JComponent, rect: Rectangle, s: String) {
            val g = graphics as Graphics2D
            val button = c as HyperlinkButton
            val model = button.getModel()
            val enabled = button.isEnabled
            if (enabled) {
                if (model.isEnabled) {
                    if (model.isPressed) {
                        button.foreground = getActiveLinkColor()
                    } else {
                        button.foreground = getLinkColor()
                    }
                }
            }
            super.paintText(g, c as JComponent, rect, s)
            if (enabled) {
                val fm = g.fontMetrics
                val x = rect.x + textShiftOffset
                val y = rect.y + fm.ascent + fm.descent + textShiftOffset - 1
                val oldStroke = g.stroke
                g.color = if (model.isEnabled) button.foreground else button.background.brighter()
                val focus = button.isFocusPainted && button.hasFocus() && !model.isArmed && !model.isPressed
                if (focus) {
                    g.stroke = STROKE_DASHED
                    val oldAntiAliasValue = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING)
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                    g.drawRect(x - 1, rect.y + 2, rect.width + 3, rect.height - 3)
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAliasValue)
                } else {
                    g.drawLine(x, y, x + rect.width - 1, y)
                }
                g.stroke = oldStroke
            }
        }

        companion object {
            private val STROKE_DASHED = BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 10f, floatArrayOf(2f, 2f), 0f)
            private val BORDER: Border = EmptyBorder(0, 3, 0, 3)

            @Suppress("UNUSED_PARAMETER")
            @JvmStatic
            fun createUI(component: JComponent?): ComponentUI = BasicLinkButtonUI()
        }
    }

}