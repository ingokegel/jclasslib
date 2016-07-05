/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.util.ExtendedJLabel
import org.gjt.jclasslib.util.HtmlDisplayTextArea
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

abstract class DetailPane<out T : Any>(private val elementClass: Class<T>, val services: BrowserServices) : JPanel() {

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

    protected fun normalLabel(text: String = "") = ExtendedJLabel(text)

    protected fun highlightLabel() = normalLabel().apply {
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

    fun getElement(treePath: TreePath): T {
        return getElementOrNull(treePath)!!
    }

    fun getElementOrNull(treePath: TreePath): T? {
        return elementClass.cast(treeNode(treePath).element)
    }

    fun treeNode(treePath: TreePath) = treePath.lastPathComponent as BrowserTreeNode

    fun updateFilter() {
        val tree = services.browserComponent.treePane.tree
        val treeNode = tree.selectionPath.lastPathComponent as BrowserTreeNode
        updateFilter(tree, treeNode)
    }

    protected open fun updateFilter(tree: JTree, treeNode: BrowserTreeNode) {
        treeNode.filterChildren { node ->
            isChildShown(node)
        }
        (tree.model as DefaultTreeModel).nodeStructureChanged(treeNode)
        tree.expandPath(tree.selectionPath)
    }

    protected open fun isChildShown(node: BrowserTreeNode) = true

    protected fun getConstantPoolEntryName(constantPoolIndex: Int): String {
        try {
            return services.classFile.getConstantPoolEntryName(constantPoolIndex)
        } catch (ex: InvalidByteCodeException) {
            return "invalid constant pool reference"
        }
    }

    companion object {
        val CPINFO_LINK_TEXT = "cp_info #"
        protected val COLOR_HIGHLIGHT = Color(128, 0, 0)
    }
}