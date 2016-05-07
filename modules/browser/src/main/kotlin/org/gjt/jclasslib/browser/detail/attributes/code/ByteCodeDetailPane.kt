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
import org.gjt.jclasslib.browser.detail.attributes.code.ByteCodeDisplay.DocumentLink
import org.gjt.jclasslib.browser.detail.attributes.code.ByteCodeDisplay.DocumentLinkType
import org.gjt.jclasslib.bytecode.Instruction
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.util.DefaultAction
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

    private val instructionToURL = HashMap<String, String>()

    private val showDescriptionAction = DefaultAction("Show Description", "Show detailed information about the selected instruction.") {
        val opcode = instructionsDropDown.selectedItem
        if (opcode != null) {
            val url = instructionToURL[opcode]
            if (url != null) {
                services.showURL(url)
            }
        }
    }

    private val instructionsDropDown = JComboBox<String>()

    private val byteCodeTextPane: JTextPane = ByteCodeTextPane()

    private val opcodeCounterTextPane: JTextPane = OpcodeCounterTextPane().apply {
        isEnabled = false
        // the following line should but does not work (see OpcodeCounterTextPane)
        autoscrolls = false
    }

    private val scrollPane: JScrollPane = JScrollPane(ScrollableShield(byteCodeTextPane)).apply {
        setRowHeaderView(opcodeCounterTextPane)
        verticalScrollBar.unitIncrement = lineHeight
    }

    init {
        name = "Bytecode"
    }

    override fun setupComponent() {
        layout = BorderLayout()
        add(Box.createHorizontalBox().apply {
            add(JPanel(FlowLayout(FlowLayout.LEFT, 6, 0)).apply {
                add(JLabel("Used instructions:"))
                add(instructionsDropDown, BorderLayout.CENTER)
                add(showDescriptionAction.createTextButton())
            })
        }, BorderLayout.SOUTH)
        add(scrollPane, BorderLayout.CENTER)
    }

    fun setCurrentInstructions(instructions: List<Instruction>) {
        instructionToURL.clear()
        val mnemonics = TreeSet<String>()
        instructions.forEach { instruction ->
            val verbose = instruction.opcode.verbose
            if (mnemonics.add(verbose)) {
                instructionToURL.put(verbose, instruction.opcode.docUrl)
            }
        }
        instructionsDropDown.model = DefaultComboBoxModel(mnemonics.toTypedArray())
    }

    override fun show(treePath: TreePath) {
        val attribute = getElement(treePath)
        val byteCodeDocument = attributeToByteCodeDocument.getOrPut(attribute) {
            ByteCodeDisplay(this, styles, attribute, services.classFile)
        }

        if (byteCodeTextPane.document !== byteCodeDocument) {
            val characterWidth: Int = getFontMetrics(styles.getFont(ByteCodeDisplay.STYLE_LINE_NUMBER)).charWidth('0')
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

    private fun link(link: DocumentLink) {
        val linkType = link.type
        val sourceOffset = link.sourceOffset
        updateHistory(sourceOffset)

        if (linkType == DocumentLinkType.CONSTANT_POOL_LINK) {
            ConstantPoolHyperlinkListener.link(services, link.index)
        } else if (linkType == DocumentLinkType.OFFSET_LINK) {
            scrollToOffset(link.index)
            val targetOffset = link.index
            updateHistory(targetOffset)
        }
    }

    private fun updateHistory(offset: Int) {
        val treePath = services.browserComponent.treePane.tree.selectionPath
        val history = services.browserComponent.history
        history.updateHistory(treePath, offset)
    }

    private val lineHeight: Int
        get() = getFontMetrics(byteCodeTextPane.font).height

    private val byteCodeDocument: ByteCodeDisplay
        get() = byteCodeTextPane.document as ByteCodeDisplay


    private inner class DocumentLinkListener(private val textPane: JTextPane) : MouseAdapter(), MouseMotionListener {
        private val defaultCursor: Cursor
        private val defaultCursorType: Int
        private val handCursor: Cursor

        init {
            textPane.addMouseListener(this)
            textPane.addMouseMotionListener(this)

            defaultCursor = Cursor.getDefaultCursor()
            defaultCursorType = defaultCursor.type
            handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        }

        override fun mouseClicked(event: MouseEvent?) {
            val position = textPane.viewToModel(event!!.point)
            val linkAttribute = getLinkAttribute(position)
            if (linkAttribute != null) {
                link(linkAttribute)
            }
        }

        override fun mouseDragged(event: MouseEvent?) {
        }

        override fun mouseMoved(event: MouseEvent?) {
            val position = textPane.viewToModel(event!!.point)
            if (textPane.cursor.type == defaultCursorType && isLink(position)) {
                textPane.cursor = handCursor
            } else if (!isLink(position)) {
                textPane.cursor = defaultCursor
            }
        }

        private fun isLink(position: Int): Boolean {
            return getLinkAttribute(position) != null
        }

        private fun getLinkAttribute(position: Int): DocumentLink? {
            val document = textPane.document as DefaultStyledDocument
            val element = document.getCharacterElement(position) as AbstractElement
            return element.getAttribute(ByteCodeDisplay.ATTRIBUTE_NAME_LINK) as DocumentLink?
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
        init {
            font = Font("MonoSpaced".intern(), 0, UIManager.getFont("TextArea.font").size)
            isEditable = false
            DocumentLinkListener(this)

            navigationFilter = object : NavigationFilter() {
                override fun getNextVisualPositionFrom(text: JTextComponent, pos: Int, bias: Position.Bias, direction: Int, biasRet: Array<out Position.Bias>) =
                        if (direction == SwingConstants.WEST || direction == SwingConstants.EAST || byteCodeTextPane.selectedText != null) {
                            super.getNextVisualPositionFrom(text, pos, bias, direction, biasRet)
                        } else {
                            val viewRect = (parent.parent as JViewport).viewRect
                            val lineHeight = lineHeight
                            val nextLineNumber = if (direction == SwingConstants.SOUTH) (viewRect.height + viewRect.y) / lineHeight else viewRect.y / lineHeight - 1
                            byteCodeDocument.getLineStartPosition(nextLineNumber)
                        }
            }
        }
    }

    private class ScrollableShield(val byteCodeTextPane: JTextPane) : JPanel() {
        init {
            layout = BorderLayout()
            add(byteCodeTextPane, BorderLayout.CENTER)
        }
    }

    companion object {
        private val origin = Rectangle(0, 0, 0, 0)
        private val LINE_NUMBERS_OFFSET = 9
        private val styles = StyleContext()
        private val attributeToByteCodeDocument = WeakHashMap<CodeAttribute, ByteCodeDisplay>()
    }
}

