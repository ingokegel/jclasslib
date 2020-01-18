/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.document

import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.util.BatchDocument
import org.gjt.jclasslib.util.documentFontFamily
import org.gjt.jclasslib.util.documentFontSize
import org.gjt.jclasslib.util.getLinkColor
import java.awt.BasicStroke
import java.awt.Color
import java.io.IOException
import java.util.*
import javax.swing.event.DocumentEvent
import javax.swing.text.*

typealias LineNumberCounts = List<Int>

abstract class AttributeDocument(protected val styles: StyleContext, protected val classFile: ClassFile) : BatchDocument(styles) {

    private val lineStartPositions = ArrayList<Int>()
    val lineNumberDocument = BatchDocument(styles)
    var lineNumberWidth: Int = 0
        private set

    init {
        putProperty("tabSize", 4)
    }

    fun getLineStartPosition(lineNumber: Int) = lineStartPositions.getOrElse(lineNumber - 1) { if (it < 0) 0 else length }

    fun modifyDocument(block: DocumentModification.() -> Unit) {
        writeLock()
        try {
            val documentModification = DocumentModification()
            documentModification.block()
            documentModification.modifiedRanges.forEach { range ->
                fireChangedUpdate(DefaultDocumentEvent(range.first, range.last - range.first, DocumentEvent.EventType.CHANGE))
            }
        } finally {
            writeUnlock()
        }
    }

    protected fun setupDocument() {
        try {
            val lineNumberCounts = addContent()
            processBatchUpdates(0)
            createLineNumberDocument(lineNumberCounts)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    protected abstract fun addContent(): LineNumberCounts?

    override fun appendBatchLineFeed(attributes: AttributeSet) {
        super.appendBatchLineFeed(attributes)
        lineStartPositions.add(length)
    }

    fun appendBatchLineFeed() {
        appendBatchLineFeed(STYLE_NORMAL)
    }

    protected fun appendString(string: String, attributes: AttributeSet) {
        try {
            appendBatchString(string, attributes)
        } catch (ex: BadLocationException) {
            ex.printStackTrace()
        }
    }

    private fun createLineNumberDocument(lineNumberCounts: LineNumberCounts?) {
        val numberOfLines = lineNumberCounts?.size ?: lineStartPositions.size
        lineNumberWidth = numberOfLines.toString().length
        try {
            for (i in 1..numberOfLines) {
                lineNumberDocument.appendBatchString(getPaddedValue(i, lineNumberWidth), STYLE_LINE_NUMBER)
                val lineNumberCount = lineNumberCounts?.get(i - 1) ?: 1
                for (j in 0 until lineNumberCount) {
                    lineNumberDocument.appendBatchLineFeed(STYLE_NORMAL)
                }
            }
            lineNumberDocument.processBatchUpdates(0)
        } catch (ex: BadLocationException) {
            ex.printStackTrace()
        }
    }

    protected fun getPaddedValue(number: Int, width: Int) = StringBuilder().apply {
        val value = number.toString()
        val valueLength = value.length
        for (i in valueLength until width) {
            append(' ')
        }
        append(value)
    }.toString()

    protected fun addConstantPoolLink(constantPoolIndex: Int, sourceOffset: Int = length) {

        val currentLinkStyle = styles.addAttribute(STYLE_LINK, ATTRIBUTE_NAME_LINK,
                ConstantPoolLink(constantPoolIndex, sourceOffset))

        appendString(" ", STYLE_NORMAL)
        appendString("#$constantPoolIndex", currentLinkStyle)
        try {
            val name = classFile.getConstantPoolEntryName(constantPoolIndex)
            if (name.isNotEmpty()) {
                appendString(" <$name>", STYLE_SMALL)
            }
        } catch (ex: InvalidByteCodeException) {
            appendString(" [INVALID]", STYLE_SMALL)
        }
    }

    interface Link
    interface DocumentLink : Link {
        val sourceOffset: Int
    }

    data class ConstantPoolLink(val constantPoolIndex: Int, override val sourceOffset: Int) : DocumentLink

    class DocumentModification {
        var modifiedRanges = mutableListOf<IntRange>()

        fun modifiedRange(range: IntRange) {
            modifiedRanges.add(range)
        }

        fun modifiedElement(element: AbstractElement) {
            modifiedRange(element.startOffset..element.endOffset)
        }
    }

    companion object {
        const val ATTRIBUTE_NAME_LINK = "attributeLink"
        const val ATTRIBUTE_NAME_HOVER_HIGHLIGHT = "hoverHighlight"

        val DOTTED_STROKE = BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0.toFloat(), floatArrayOf(3.toFloat(), 4.toFloat()), 0.0f)
        const val TAB = "    "

        val STYLE_NORMAL = style()
        val STYLE_SMALL = style {
            fontSize -= 1
        }
        val STYLE_LINK = style {
            foreground = getLinkColor()
            bold = true
            underline = true
        }
        val STYLE_LINE_NUMBER = style {
            fontSize -= 2
        }

        fun style(init: StyleBuilder.() -> Unit = {}): AttributeSet {
            val styleBuilder = StyleBuilder()
            // The next line explicitly sets the font size on the attribute set, this is needed for HiDPI displays

            styleBuilder.fontSize = documentFontSize
            documentFontFamily?.let {
                styleBuilder.fontFamily = it
            }
            styleBuilder.init()
            return styleBuilder.attributeSet
        }

        class StyleBuilder {
            val attributeSet = SimpleAttributeSet()

            var fontFamily: String
                get() = StyleConstants.getFontFamily(attributeSet)
                set(fontFamily) {
                    StyleConstants.setFontFamily(attributeSet, fontFamily)
                }

            var fontSize: Int
                get() = StyleConstants.getFontSize(attributeSet)
                set(fontSize) {
                    StyleConstants.setFontSize(attributeSet, fontSize)
                }

            var foreground: Color
                get() = StyleConstants.getForeground(attributeSet)
                set(color) {
                    StyleConstants.setForeground(attributeSet, color)
                }

            var bold: Boolean
                get() = StyleConstants.isBold(attributeSet)
                set(bold) {
                    StyleConstants.setBold(attributeSet, bold)
                }

            var underline: Boolean
                get() = StyleConstants.isUnderline(attributeSet)
                set(bold) {
                    StyleConstants.setUnderline(attributeSet, bold)
                }

            fun attribute(name: String, value: Any) {
                attributeSet.addAttribute(name, value)
            }
        }
    }
}
