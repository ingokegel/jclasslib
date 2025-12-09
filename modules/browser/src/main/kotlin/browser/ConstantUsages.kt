/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.Structure
import org.gjt.jclasslib.util.*
import java.awt.Component
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.TreePath

fun findConstantUsages(browserComponent: BrowserComponent, constant: Constant) {
    val rootNode = browserComponent.treePane.root
    val usagesRootNode = FoundUsageNode(rootNode, false)
    createUsageNodes(rootNode, usagesRootNode) { node ->
        (node.element as? Structure)?.isConstantUsed(constant, browserComponent.services.classFile) ?: false
    }
    pruneTree(usagesRootNode)
    if (usagesRootNode.childCount > 0) {
        val targetNode = FoundUsagesDialog(usagesRootNode, browserComponent.getParentWindow()).select()
        if (targetNode != null) {
            val targetPath = TreePath(targetNode.sourceNode.path)
            browserComponent.treePane.tree.apply {
                selectionPath = targetPath
                scrollPathToVisible(targetPath)
            }
        }
    } else {
        showNoUsagesFoundMessage(browserComponent)
    }
}

fun showNoUsagesFoundMessage(browserComponent: BrowserComponent) {
    alertFacade.showMessage(browserComponent, getString("no.usages.found"), AlertType.INFORMATION)
}

private fun createUsageNodes(
    sourceNode: BrowserTreeNode, targetNode: FoundUsageNode,
    filter: (BrowserTreeNode) -> Boolean
) {
    for (sourceChildNode in sourceNode.allChildren) {
        val targetChildNode = FoundUsageNode(sourceChildNode, filter(sourceChildNode))
        targetNode.add(targetChildNode)
        createUsageNodes(sourceChildNode, targetChildNode, filter)
    }
}

private fun pruneTree(node: FoundUsageNode): Boolean {
    for (i in node.childCount - 1 downTo 0) {
        val childNode = node.getChildAt(i) as FoundUsageNode
        val childUsed = pruneTree(childNode)
        if (!childUsed) {
            node.remove(i)
        }
    }
    return node.used || node.childCount > 0
}

private class FoundUsageNode(val sourceNode: BrowserTreeNode, val used: Boolean) :
    BrowserTreeNode(sourceNode.userObject.toString(), sourceNode.type, sourceNode.element)

private class FoundUsagesDialog(rootNode: FoundUsageNode, parentWindow: Window?) :
    SelectionDialog<FoundUsageNode>(parentWindow, getString("found.usages.title")) {
    private val tree = JTree(rootNode).apply {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && isUsageSelected()) {
                    doOk()
                }
            }
        })
        addTreeSelectionListener {
            updateEnabled()
        }
        cellRenderer = object : DefaultTreeCellRenderer() {
            override fun getTreeCellRendererComponent(
                tree: JTree?,
                value: Any?,
                selected: Boolean,
                expanded: Boolean,
                leaf: Boolean,
                row: Int,
                hasFocus: Boolean
            ): Component {
                return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus).apply {
                    isEnabled = selected || (value as? FoundUsageNode)?.used ?: false
                }
            }

            override fun getDisabledIcon() = icon
        }
        isRootVisible = false
        showsRootHandles = true
        expandAll()
        updateEnabled()
    }

    private fun JTree.updateEnabled() {
        okAction.isEnabled = isUsageSelected()
    }

    private fun JTree.isUsageSelected(): Boolean = (selectionPath?.lastPathComponent as? FoundUsageNode)?.used == true

    init {
        setupComponent()
    }

    override val okButtonText: String
        get() = getString("action.show")

    override val selectedItem: FoundUsageNode?
        get() = tree.selectionPath?.lastPathComponent as? FoundUsageNode

    override fun addContent(component: JComponent) {
        with(component) {
            layout = MigLayout("wrap", "[grow]")
            add(JScrollPane(tree), "pushy, grow")
        }
        setSize(600, 400)
    }
}
