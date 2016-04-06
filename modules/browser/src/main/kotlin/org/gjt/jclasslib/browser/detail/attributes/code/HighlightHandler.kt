/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.code

import java.awt.Graphics2D
import java.awt.Point
import java.awt.Shape
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.font.TextLayout

class HighlightHandler(private val byteCodeDisplay: ByteCodeDisplay) : MouseAdapter(), MouseMotionListener {

    private var hlStartLine = 0
    private var hlStartOffset = 0
    private var hlEndLine = 0
    private var hlEndOffset = 0

    init {
        byteCodeDisplay.addMouseListener(this)
        byteCodeDisplay.addMouseMotionListener(this)
    }

    fun reset() {
        setAnchor(Point(0, 0), true)
    }

    override fun mousePressed(event: MouseEvent) {
        setAnchor(event.point, true)
    }

    override fun mouseDragged(event: MouseEvent) {
        setAnchor(event.point, false)
    }

    override fun mouseMoved(event: MouseEvent?) {
    }

    val selectedText: String?
        get() {
            if (hlStartLine == hlEndLine && hlStartOffset == hlEndOffset) {
                return null
            }

            val startLine: Int
            val startOffset: Int
            val endLine: Int
            val endOffset: Int
            if (hlEndLine >= hlStartLine) {
                startLine = hlStartLine
                endLine = hlEndLine
                startOffset = hlStartOffset
                endOffset = hlEndOffset
            } else {
                startLine = hlEndLine
                endLine = hlStartLine
                startOffset = hlEndOffset
                endOffset = hlStartOffset
            }
            val buffer = StringBuilder()
            val it = byteCodeDisplay.textLines.listIterator(startLine)
            for (i in startLine..endLine) {
                val lineText = it.next()
                if (i == startLine && i == endLine) {
                    buffer.append(lineText.substring(Math.min(startOffset, endOffset), Math.max(startOffset, endOffset)))
                } else if (i == startLine) {
                    buffer.append(lineText.substring(startOffset))
                    buffer.append('\n')
                } else if (i == endLine) {
                    buffer.append(lineText.substring(0, endOffset))
                } else {
                    buffer.append(lineText)
                    buffer.append('\n')
                }
            }

            return buffer.toString()
        }

    fun drawHighlight(line: Int, textLayout: TextLayout, g: Graphics2D) {

        val startLine: Int
        val startOffset: Int
        val endLine: Int
        val endOffset: Int
        if (hlEndLine >= hlStartLine) {
            startLine = hlStartLine
            endLine = hlEndLine
            startOffset = hlStartOffset
            endOffset = hlEndOffset
        } else {
            startLine = hlEndLine
            endLine = hlStartLine
            startOffset = hlEndOffset
            endOffset = hlStartOffset
        }
        if (line < startLine || line > endLine || (startLine == endLine && startOffset == endOffset)) {
            return
        }

        val shape = getHighlightShape(line, startLine, startOffset, endLine, endOffset, textLayout) ?: return
        val oldPaint = g.paint
        g.setXORMode(byteCodeDisplay.scrollPane.viewport.background)

        val sourceY = line * byteCodeDisplay.lineHeight + textLayout.ascent.toInt()
        g.translate(0, sourceY)
        g.paint = byteCodeDisplay.foreground
        g.fill(shape)
        g.paint = oldPaint
        g.setPaintMode()
        g.translate(0, -sourceY)

    }

    private fun setAnchor(point: Point, start: Boolean) {
        val scrollPane = byteCodeDisplay.scrollPane
        val lines = byteCodeDisplay.textLines
        val lineHeight = byteCodeDisplay.lineHeight

        if (lineHeight == 0 || lines.size == 0) {
            return
        }

        val x = point.x - ByteCodeDisplay.MARGIN_X + scrollPane.x
        val y = point.y - ByteCodeDisplay.MARGIN_Y + scrollPane.y

        val (line, charIndex) = calculatePosition(x, y, lineHeight)

        if (start) {
            hlStartLine = line
            hlStartOffset = charIndex
        }

        hlEndLine = line
        hlEndOffset = charIndex
        scrollPane.viewport.repaint()
    }

    private fun calculatePosition(x: Int, y: Int, lineHeight: Int): Pair<Int, Int> {
        val lines = byteCodeDisplay.textLines
        val line = y / lineHeight
        if (line < 0) {
            return 0 to 0
        } else if (line >= lines.size) {
            return lines.size - 1 to lines.last().length
        } else {
            val textLayout = byteCodeDisplay.getOrCreateTextLayout(line)
            if (x >= textLayout.advance) {
                return line to lines[line].length
            } else {
                val textHitInfo = textLayout.hitTestChar(x.toFloat(), (y - line * lineHeight).toFloat())
                return line to Math.max(0, textHitInfo.charIndex)
            }
        }
    }

    private fun getHighlightShape(line: Int, startLine: Int, startOffset: Int, endLine: Int, endOffset: Int, textLayout: TextLayout): Shape? {
        return textLayout.getLogicalHighlightShape(getHighlightStartOffset(startOffset, line, startLine, endLine), getHighlightEndOffset(endOffset, line, startLine, endLine, textLayout))
    }

    private fun getHighlightStartOffset(startOffset: Int, line: Int, startLine: Int, endLine: Int) = if (line == startLine || startLine == endLine) {
        startOffset
    } else {
        0
    }

    private fun getHighlightEndOffset(endOffset: Int, line: Int, startLine: Int, endLine: Int, textLayout: TextLayout): Int {
        return if (line == endLine || startLine == endLine) {
            endOffset
        } else {
            textLayout.characterCount
        }
    }

}
