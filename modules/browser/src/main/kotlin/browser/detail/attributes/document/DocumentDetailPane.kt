/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */


package org.gjt.jclasslib.browser.detail.attributes.document

import org.gjt.jclasslib.browser.BrowserHistory
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener
import org.gjt.jclasslib.browser.detail.DetailPaneWithKeyValues
import org.gjt.jclasslib.browser.detail.attributes.document.AttributeDocument.*
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.util.getActiveLinkColor
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.util.*
import javax.swing.*
import javax.swing.text.*
import javax.swing.text.AbstractDocument.AbstractElement
import javax.swing.tree.TreePath

@Suppress("DEPRECATION") // Remove after requiring at least Java 9
abstract class DocumentDetailPane<T : AttributeInfo, out D: AttributeDocument>(elementClass: Class<T>, private val documentClass: Class<D>, services: BrowserServices) : DetailPaneWithKeyValues<T>(elementClass, services) {

    protected val textPane = AttributeTextPane()

    private val opcodeCounterTextPane = OpcodeCounterTextPane().apply {
        isEnabled = false
        // text attribute colors are ignored for disabled text panes
        disabledTextColor = Color(128, 128, 128)
        // the following line should but does not work (see OpcodeCounterTextPane)
        autoscrolls = false
    }

    private val scrollPane: JScrollPane = JScrollPane(textPane).apply {
        setRowHeaderView(opcodeCounterTextPane)
    }

    override fun setupComponent() {
        super.setupComponent()
        add(scrollPane, "dock center")
    }

    override fun show(treePath: TreePath) {
        super.show(treePath)
        val attribute = getElement(treePath)
        val detailDocument = attributeToDocument.getOrPut(attribute) {
            createDocument(styles, attribute, services.classFile)
        }

        if (textPane.document !== detailDocument) {
            val characterWidth: Int = getFontMetrics(styles.getFont(AttributeDocument.STYLE_LINE_NUMBER)).charWidth('0')
            val opcodeCounterWidth = characterWidth * detailDocument.lineNumberWidth + LINE_NUMBERS_OFFSET

            withWaitCursor {
                opcodeCounterTextPane.apply {
                    document = detailDocument.lineNumberDocument
                    preferredWidth = opcodeCounterWidth
                }
                textPane.apply {
                    document = detailDocument
                    caretPosition = 0
                    scrollRectToVisible(origin)
                }
            }
        }
    }

    abstract fun createDocument(styles: StyleContext, attribute: T, classFile: ClassFile): D
    abstract fun offsetToPosition(offset: Int) : Int

    private fun withWaitCursor(function: () -> Unit) {
        val browserComponent = services.browserComponent
        browserComponent.cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
        try {
            SwingUtilities.invokeLater(function)
        } finally {
            browserComponent.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
        }
    }

    protected open fun linkTriggered(link: Link) {
        if (link is DocumentLink) {
            val sourceOffset = link.sourceOffset
            updateHistory(sourceOffset)
        }
        when (link) {
            is ConstantPoolLink -> ConstantPoolHyperlinkListener.link(services, link.constantPoolIndex)
        }
    }

    protected fun updateHistory(offset: Int) {
        val treePath = services.browserComponent.treePane.tree.selectionPath ?: return
        val history = services.browserComponent.history
        history.addHistoryEntry(treePath, object: BrowserHistory.Resetter {
            override fun reset() {
                makeVisible()
                scrollToOffset(offset)
            }

            override fun toString() = "offset $offset"
        })
    }

    protected open fun makeVisible() {

    }

    fun scrollToOffset(offset: Int) {
        val position = offsetToPosition(offset)
        try {
            val target = textPane.modelToView(position)
            target.height = textPane.height
            textPane.caretPosition = position
            textPane.scrollRectToVisible(target)

        } catch (ex: BadLocationException) {
        }
    }

    private val lineHeight: Int
        get() = getFontMetrics(textPane.font).height

    protected val attributeDocument: D
        get() = documentClass.cast(textPane.document)


    private inner class DocumentLinkListener(private val textPane: JTextPane) : MouseAdapter(), MouseMotionListener {
        private var activeElement : AbstractElement? = null
        private var oldAttributes: AttributeSet? = null
        private var activeHighlight: Any? = null

        init {
            textPane.addMouseListener(this)
            textPane.addMouseMotionListener(this)
        }

        override fun mousePressed(event: MouseEvent) {
            val element = getTextElement(event)
            if (element.isDocumentLink) {
                this@DocumentDetailPane.textPane.preventDrag = true
                oldAttributes = element.copyAttributes()
                attributeDocument.modifyDocument {
                    element.addAttribute(StyleConstants.Foreground, getActiveLinkColor())
                    modifiedElement(element)
                }
            }
        }

        override fun mouseReleased(event: MouseEvent) {
            activeElement?.let { element ->
                if (element.isDocumentLink) {
                    this@DocumentDetailPane.textPane.preventDrag = false
                    attributeDocument.modifyDocument {
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
                textPane.cursor = if (textElement.isLink) Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) else Cursor.getDefaultCursor()
            }
        }

        override fun mouseExited(e: MouseEvent?) {
            if (!this@DocumentDetailPane.textPane.preventDrag) {
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
            get() = this.getAttribute(AttributeDocument.ATTRIBUTE_NAME_LINK) as Link?

        private val AbstractElement.hoverHighlight: Stroke?
            get() = this.getAttribute(AttributeDocument.ATTRIBUTE_NAME_HOVER_HIGHLIGHT) as Stroke?

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
    private inner class OpcodeCounterTextPane : AttributeTextPane() {

        var preferredWidth : Int = 0

        override fun processMouseMotionEvent(e: MouseEvent) {
        }

        override fun getPreferredSize(): Dimension {
            return super.getPreferredSize().apply { width = preferredWidth }
        }
    }

    protected open inner class AttributeTextPane : JTextPane() {
        var preventDrag: Boolean = false

        init {
            editorKit = AttributeEditorKit()
            font = Font("MonoSpaced".intern(), 0, UIManager.getFont("TextArea.font").size)
            background = UIManager.getColor("TextField.background")
            isEditable = false
            DocumentLinkListener(this)

            navigationFilter = object : NavigationFilter() {
                override fun getNextVisualPositionFrom(text: JTextComponent, pos: Int, bias: Position.Bias, direction: Int, biasRet: Array<out Position.Bias>) =
                        if (direction == SwingConstants.WEST || direction == SwingConstants.EAST || textPane.selectedText != null) {
                            super.getNextVisualPositionFrom(text, pos, bias, direction, biasRet)
                        } else {
                            val viewRect = (parent as JViewport).viewRect
                            val lineHeight = lineHeight
                            val nextLineNumber = if (direction == SwingConstants.SOUTH) (viewRect.height + viewRect.y) / lineHeight else viewRect.y / lineHeight - 1
                            attributeDocument.getLineStartPosition(nextLineNumber)
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
        private const val LINE_NUMBERS_OFFSET = 9

        private data class FontInfo(val family: String, val style: Int, val size: Int)
        private val styles = object : StyleContext() {

            private val fontCache = HashMap<FontInfo, Font>()

            // Font queries in StyleContext are very slow because they call String.intern().
            // Caching in derived class fixes the problem.
            override fun getFont(family: String, style: Int, size: Int) =
                    fontCache.getOrPut(FontInfo(family, style, size)) {
                        super.getFont(family, style, size)
                    }
        }
        private val attributeToDocument = WeakHashMap<AttributeInfo, AttributeDocument>()

    }

}
