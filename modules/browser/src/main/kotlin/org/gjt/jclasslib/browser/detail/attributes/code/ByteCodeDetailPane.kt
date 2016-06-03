/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.browser.detail.attributes.code.ByteCodeDocument.*
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.util.*
import javax.swing.*
import javax.swing.text.*
import javax.swing.text.AbstractDocument.AbstractElement
import javax.swing.tree.TreePath

class ByteCodeDetailPane(services: BrowserServices) : DetailPane<CodeAttribute>(CodeAttribute::class.java, services) {

    private val byteCodeTextPane = ByteCodeTextPane()

    private val opcodeCounterTextPane: JTextPane = OpcodeCounterTextPane().apply {
        isEnabled = false
        // the following line should but does not work (see OpcodeCounterTextPane)
        autoscrolls = false
    }

    private val scrollPane: JScrollPane = JScrollPane(byteCodeTextPane).apply {
        setRowHeaderView(opcodeCounterTextPane)
    }

    init {
        name = "Bytecode"
    }

    override fun setupComponent() {
        layout = BorderLayout()
        add(scrollPane, BorderLayout.CENTER)
    }

    override fun show(treePath: TreePath) {
        val attribute = getElement(treePath)
        val byteCodeDocument = attributeToByteCodeDocument.getOrPut(attribute) {
            ByteCodeDocument(styles, attribute, services.classFile)
        }

        if (byteCodeTextPane.document !== byteCodeDocument) {
            val characterWidth: Int = getFontMetrics(styles.getFont(ByteCodeDocument.STYLE_LINE_NUMBER)).charWidth('0')
            val opcodeCounterSize = Dimension(characterWidth * byteCodeDocument.opcodeCounterWidth + LINE_NUMBERS_OFFSET, 0)

            withWaitCursor {
                opcodeCounterTextPane.apply {
                    document = byteCodeDocument.opcodeCounterDocument
                    minimumSize = opcodeCounterSize
                    preferredSize = opcodeCounterSize
                }
                byteCodeTextPane.apply {
                    document = byteCodeDocument
                    caretPosition = 0
                    scrollRectToVisible(origin)
                }
            };
        }
    }

    private fun withWaitCursor(function: () -> Unit) {
        val browserComponent = services.browserComponent
        browserComponent.cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
        try {
            SwingUtilities.invokeLater({
                function.invoke()
            })
        } finally {
            browserComponent.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
        }
    }

    fun scrollToOffset(offset: Int) {
        val position = byteCodeDocument.getPosition(offset)
        try {
            val target = byteCodeTextPane.modelToView(position)
            target.height = byteCodeTextPane.height
            byteCodeTextPane.caretPosition = position
            byteCodeTextPane.scrollRectToVisible(target)

        } catch (ex: BadLocationException) {
        }
    }

    private fun linkTriggered(link: Link) {
        if (link is DocumentLink) {
            val sourceOffset = link.sourceOffset
            updateHistory(sourceOffset)
        }
        when (link) {
            is ConstantPoolLink -> ConstantPoolHyperlinkListener.link(services, link.constantPoolIndex)
            is OffsetLink -> {
                scrollToOffset(link.targetOffset)
                updateHistory(link.targetOffset)
            }
            is SpecLink -> {
                services.showURL(link.opcode.docUrl)
            }
        }
    }

    private fun updateHistory(offset: Int) {
        val treePath = services.browserComponent.treePane.tree.selectionPath
        val history = services.browserComponent.history
        history.addHistoryEntry(treePath, offset)
    }

    private val lineHeight: Int
        get() = getFontMetrics(byteCodeTextPane.font).height

    private val byteCodeDocument: ByteCodeDocument
        get() = byteCodeTextPane.document as ByteCodeDocument


    private inner class DocumentLinkListener(private val textPane: JTextPane) : MouseAdapter(), MouseMotionListener {
        private val defaultCursor: Cursor
        private val defaultCursorType: Int
        private val handCursor: Cursor
        private var activeElement : AbstractElement? = null
        private var oldAttributes: AttributeSet? = null
        private var activeHighlight: Any? = null

        init {
            textPane.addMouseListener(this)
            textPane.addMouseMotionListener(this)

            defaultCursor = Cursor.getDefaultCursor()
            defaultCursorType = defaultCursor.type
            handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        }

        override fun mousePressed(event: MouseEvent) {
            val element = getTextElement(event)
            if (element.isDocumentLink) {
                byteCodeTextPane.preventDrag = true
                oldAttributes = element.copyAttributes()
                byteCodeDocument.modifyDocument {
                    element.addAttribute(StyleConstants.Foreground, ByteCodeDocument.ACTIVE_LINK_COLOR)
                    modifiedElement(element)
                }
            }
        }

        override fun mouseReleased(event: MouseEvent) {
            activeElement?.let { element ->
                if (element.isDocumentLink) {
                    byteCodeTextPane.preventDrag = false
                    byteCodeDocument.modifyDocument {
                        element.removeAttribute(element)
                        element.addAttributes(oldAttributes)
                        modifiedElement(element)
                    }
                    oldAttributes = null
                }
            }
        }

        override fun mouseClicked(event: MouseEvent) {
            val link = getTextElement(event).link
            if (link != null) {
                removeActiveHighlight()
                linkTriggered(link)
            }
        }

        override fun mouseDragged(event: MouseEvent) {
        }

        override fun mouseMoved(event: MouseEvent) {
            val textElement = getTextElement(event)
            if (textElement !== activeElement) {
                removeActiveHighlight()
                activeElement = textElement
                textElement.mouseEnter()
                textPane.cursor = if (textElement.isLink) handCursor else defaultCursor
            }
        }

        override fun mouseExited(e: MouseEvent?) {
            if (!byteCodeTextPane.preventDrag) {
                removeActiveHighlight()
                activeElement = null
            }
        }

        private fun getTextElement(event: MouseEvent): AbstractElement {
            val position = textPane.viewToModel(event.point)
            val document = textPane.document as DefaultStyledDocument
            return document.getCharacterElement(position) as AbstractElement
        }

        private val AbstractElement.link: Link?
            get() = this.getAttribute(ByteCodeDocument.ATTRIBUTE_NAME_LINK) as Link?

        private val AbstractElement.hoverHighlight: Stroke?
            get() = this.getAttribute(ByteCodeDocument.ATTRIBUTE_NAME_HOVER_HIGHLIGHT) as Stroke?

        private val AbstractElement.isLink : Boolean
            get() = this.link != null

        private val AbstractElement.isDocumentLink : Boolean
            get() = this.link is DocumentLink

        private fun AbstractElement.mouseEnter() {
            this.hoverHighlight?.let { stroke ->
                activeHighlight = textPane.highlighter.addHighlight(this.startOffset, this.endOffset, LinkHighlightPainter(stroke))
            }
        }

        private fun removeActiveHighlight() {
            activeHighlight?.let { highlight ->
                textPane.highlighter.removeHighlight(highlight)
                activeHighlight = null
            }
        }
    }

    // setAutoScroll(false) does not successfully remove the auto-scroller
    // set by BasicTextUI. Since OpcodeCounterTextPane should not be
    // scrollable by dragging, mouse motion events are ignored
    private inner class OpcodeCounterTextPane : ByteCodeTextPane() {
        override fun processMouseMotionEvent(e: MouseEvent) {
        }
    }

    private inner open class ByteCodeTextPane : JTextPane() {
        var preventDrag: Boolean = false

        init {
            editorKit = ByteCodeEditorKit()
            font = Font("MonoSpaced".intern(), 0, UIManager.getFont("TextArea.font").size)
            isEditable = false
            DocumentLinkListener(this)

            navigationFilter = object : NavigationFilter() {
                override fun getNextVisualPositionFrom(text: JTextComponent, pos: Int, bias: Position.Bias, direction: Int, biasRet: Array<out Position.Bias>) =
                        if (direction == SwingConstants.WEST || direction == SwingConstants.EAST || byteCodeTextPane.selectedText != null) {
                            super.getNextVisualPositionFrom(text, pos, bias, direction, biasRet)
                        } else {
                            val viewRect = (parent as JViewport).viewRect
                            val lineHeight = lineHeight
                            val nextLineNumber = if (direction == SwingConstants.SOUTH) (viewRect.height + viewRect.y) / lineHeight else viewRect.y / lineHeight - 1
                            byteCodeDocument.getLineStartPosition(nextLineNumber)
                        }
            }
        }

        override fun processMouseMotionEvent(e: MouseEvent) {
            if (preventDrag && e.id == MouseEvent.MOUSE_DRAGGED) {
                return
            }
            super.processMouseMotionEvent(e)
        }

    }

    class LinkHighlightPainter(private val underlineStroke: Stroke) : Highlighter.HighlightPainter {
        override fun paint(g: Graphics, startOffset: Int, endOffset: Int, bounds: Shape, c: JTextComponent) {
            try {
                val textUi = c.ui
                val startRect = textUi.modelToView(c, startOffset)
                val endRect = textUi.modelToView(c, endOffset)
                val totalRect = startRect.union(endRect)

                val y = totalRect.y + totalRect.height - 1
                (g as Graphics2D).apply {
                    val oldStroke = stroke
                    val oldColor = color
                    stroke = underlineStroke
                    color = c.foreground
                    drawLine(totalRect.x, y, totalRect.x + totalRect.width, y)
                    stroke = oldStroke
                    color = oldColor
                }
            } catch (e: BadLocationException) {
            }
        }
    }

    companion object {
        private val origin = Rectangle(0, 0, 0, 0)
        private val LINE_NUMBERS_OFFSET = 9
        private val styles = object : StyleContext() {

            data class FontInfo(val family: String, val style: Int, val size: Int)

            private val fontCache = HashMap<FontInfo, Font>()

            // Font queries in StyleContext are very slow because they call String.intern().
            // Caching in derived class fixes the problem.
            override fun getFont(family: String, style: Int, size: Int) =
                    fontCache.getOrPut(FontInfo(family, style, size)) {
                        super.getFont(family, style, size)
                    }
        }
        private val attributeToByteCodeDocument = WeakHashMap<CodeAttribute, ByteCodeDocument>()

    }

}
