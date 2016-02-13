/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.font.FontRenderContext
import java.awt.font.TextAttribute
import java.awt.font.TextLayout
import java.text.AttributedCharacterIterator
import java.text.AttributedString
import java.util.*
import javax.swing.JPanel
import javax.swing.UIManager

class CounterDisplay(val byteCodeDisplay: ByteCodeDisplay) : JPanel() {

    private val maxCount: Int
        get() = byteCodeDisplay.lineCount
    private val lineHeight: Int
        get() = byteCodeDisplay.lineHeight
    private val ascent: Int
        get() = byteCodeDisplay.ascent
    private val maxChars: Int
        get() = Math.max(1, maxCount.toString().length)
    private val frc: FontRenderContext
        get() = (byteCodeDisplay.graphics as Graphics2D).fontRenderContext

    init {
        border = ByteCodeDisplay.BORDER
        isDoubleBuffered = false
        isOpaque = false
    }

    override fun addNotify() {
        super.addNotify()
        val textLayout = TextLayout(getCharacterIterator(maxCount), frc)
        preferredSize = Dimension(textLayout.advance.toInt() + 2 * ByteCodeDisplay.MARGIN_X, maxCount * lineHeight + 2 * ByteCodeDisplay.MARGIN_Y)
    }

    private fun getCharacterIterator(number: Int): AttributedCharacterIterator {
        return AttributedString(ByteCodeDisplay.getPaddedValue(number, maxChars), STYLE).iterator
    }

    override fun paintComponent(graphics: Graphics) {
        if (maxCount == 0 || lineHeight == 0) {
            return
        }
        (graphics as Graphics2D).apply {
            translate(ByteCodeDisplay.MARGIN_X, ByteCodeDisplay.MARGIN_Y)
            val oldPaint = paint
            paint = COLOR_BACKGROUND
            fill(graphics.getClipBounds())
            paint = oldPaint
            paintLines(this)
            translate(-ByteCodeDisplay.MARGIN_X, -ByteCodeDisplay.MARGIN_Y)
        }
    }

    private fun paintLines(g: Graphics2D) {
        val clipBounds = g.getClipBounds()
        val startLine = Math.max(0, clipBounds.y / lineHeight - 1)
        val endLine = Math.min(maxCount, (clipBounds.y + clipBounds.height) / lineHeight + 1)
        for (i in startLine..endLine - 1) {
            TextLayout(getCharacterIterator(i + 1), frc).draw(g, 0f, (i * lineHeight + ascent).toFloat())
        }
    }

    companion object {
        private val STYLE = HashMap<TextAttribute, Any>().apply {
            val baseFont = UIManager.getFont("TextArea.font")
            put(TextAttribute.FAMILY, baseFont.family)
            put(TextAttribute.SIZE, (baseFont.size - 2).toFloat())
            put(TextAttribute.FOREGROUND, Color(92, 92, 92))

        }
        private val COLOR_BACKGROUND = UIManager.getColor("Panel.background")
    }
}
