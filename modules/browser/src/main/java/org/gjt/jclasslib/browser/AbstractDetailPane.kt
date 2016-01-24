/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.util.ExtendedJLabel
import org.gjt.jclasslib.util.HtmlDisplayTextArea

import javax.swing.*
import javax.swing.tree.TreePath
import java.awt.*
import java.awt.event.MouseListener
import java.util.HashMap

abstract class AbstractDetailPane(val browserServices: BrowserServices) : JPanel() {

    private val labelToMouseListener = HashMap<ExtendedJLabel, MouseListener>()

    abstract fun show(treePath: TreePath)
    protected abstract fun setupComponent()

    open val clipboardText: String?
        get() = null

    val displayComponent: JComponent by lazy {
        setupComponent()
        wrapper
    }

    protected open val wrapper: JComponent
        get() = this

    @JvmOverloads protected fun normalLabel(text: String = "") = ExtendedJLabel(text)

    protected fun highlightLabel(): ExtendedJLabel = normalLabel().apply {
        foreground = COLOR_HIGHLIGHT
    }

    protected fun highlightTextArea() = HtmlDisplayTextArea().apply {
        foreground = COLOR_HIGHLIGHT
    }

    protected fun linkLabel(): ExtendedJLabel = normalLabel().apply {
        foreground = HtmlDisplayTextArea.COLOR_LINK
        isRequestFocusEnabled = true
        isUnderlined = true
    }

    protected fun getElement(treePath: TreePath): Any? {
        return (treePath.lastPathComponent as BrowserTreeNode).element
    }

    protected fun getAttribute(path: TreePath): AttributeInfo {
        return getElement(path) as AttributeInfo
    }

    protected fun getConstantPoolEntryName(constantPoolIndex: Int): String {
        try {
            return browserServices.classFile.getConstantPoolEntryName(constantPoolIndex)
        } catch (ex: InvalidByteCodeException) {
            return "invalid constant pool reference"
        }
    }

    protected fun constantPoolHyperlink(value: ExtendedJLabel, comment: ExtendedJLabel?, constantPoolIndex: Int) {
        value.apply {
            text = CPINFO_LINK_TEXT + constantPoolIndex
            setupMouseListener(ConstantPoolHyperlinkListener(browserServices, constantPoolIndex))
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        }
        comment?.applyComment(constantPoolIndex)
    }

    protected fun classAttributeIndexHyperlink(value: ExtendedJLabel,
                                               comment: ExtendedJLabel?,
                                               index: Int,
                                               attributeInfoClass: Class<out AttributeInfo>,
                                               valueText: String) {
        value.apply {
            text = valueText + index
            setupMouseListener(ClassAttributeHyperlinkListener(browserServices, index, attributeInfoClass))
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        }
        comment?.applyComment(index)
    }

    private fun ExtendedJLabel.applyComment(constantPoolIndex: Int) {
        toolTipText = text
        text = "<" + getConstantPoolEntryName(constantPoolIndex) + ">"
    }

    private fun ExtendedJLabel.setupMouseListener(mouseListener: MouseListener) {
        labelToMouseListener[this]?.let { removeMouseListener(it) }
        addMouseListener(mouseListener)
        labelToMouseListener.put(this, mouseListener)
    }

    companion object {
        @JvmField // TODO remove annotation
        val CPINFO_LINK_TEXT = "cp_info #"
        protected val COLOR_HIGHLIGHT = Color(128, 0, 0)
    }
}