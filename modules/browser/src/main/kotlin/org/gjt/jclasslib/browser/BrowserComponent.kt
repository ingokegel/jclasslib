/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.*
import org.gjt.jclasslib.structures.*
import org.gjt.jclasslib.util.SplitDirection
import org.gjt.jclasslib.util.SplitterFacade
import org.gjt.jclasslib.util.splitterFactory
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.TreePath

class BrowserComponent(private val services: BrowserServices) : JComponent(), TreeSelectionListener {
    val history: BrowserHistory = BrowserHistory(services)
    val detailPane: BrowserDetailPane = BrowserDetailPane(services)
    val treePane: BrowserTreePane = BrowserTreePane(services)

    private val splitPane: SplitterFacade = splitterFactory(SplitDirection.HORIZONTAL, treePane, detailPane)

    init {
        layout = BorderLayout()
        add(splitPane.component, BorderLayout.CENTER)
        treePane.tree.addTreeSelectionListener(this)
    }

    var browserPath: BrowserPath?
        get() {
            val selectionPath = treePane.tree.selectionPath
            if (selectionPath == null || selectionPath.pathCount < 2) {
                return null
            }
            return createBrowserPath(selectionPath)

        }
        set(browserPath) {
            if (browserPath == null || browserPath.pathComponents.size == 0) {
                treePane.tree.addSelectionRow(0)
            } else {
                val path = buildPath(browserPath.pathComponents)
                treePane.tree.apply {
                    expandPath(path)
                    selectionPath = path
                    scrollPathToVisible(path)
                }
            }
        }

    val title: String
        get() = services.classFile.simpleClassName

    private fun createBrowserPath(selectionPath: TreePath) = BrowserPath().apply {
        try {
            selectionPath.path.drop(1).map { it as BrowserTreeNode }.forEachIndexed { i, node ->
                val element = node.element
                if (i == 0) {
                    addPathComponent(CategoryHolder(node.type))
                } else {
                    when (node.type) {
                        NodeType.METHOD -> {
                            addReferenceHolder(element as MethodInfo)
                        }
                        NodeType.FIELD -> {
                            addReferenceHolder(element as FieldInfo)
                        }
                        NodeType.ATTRIBUTE -> {
                            addPathComponent(AttributeHolder((element as AttributeInfo).name))
                        }
                        NodeType.CONSTANT_POOL_ENTRY -> {
                            if (detailPane.constantPoolDetailPane.filterPane.isShowAll) {
                                addIndexHolder(node)
                            }
                        }
                        else -> {
                            addIndexHolder(node)
                        }
                    }
                }
            }
        } catch (ex: InvalidByteCodeException) {
        }
    }

    private fun buildPath(pathComponents: List<PathComponent>): TreePath {
        val nodes = mutableListOf(treePane.root)
        for (pathComponent in pathComponents) {
            val node = nodes.last().firstOrNull { pathComponent.matches(it) }
            if (node != null) {
                nodes.add(node)
            } else {
                break
            }
        }
        return TreePath(nodes.toTypedArray())
    }

    fun rebuild() {
        val browserPath = browserPath
        reset()
        if (browserPath != null) {
            this.browserPath = browserPath
        }
    }

    fun reset() {
        val tree = treePane.tree
        tree.removeTreeSelectionListener(this)
        treePane.rebuild()
        history.clear()
        tree.addTreeSelectionListener(this)
        checkSelection()
    }

    fun checkSelection() {
        val tree = treePane.tree
        if (tree.selectionPath == null) {
            val rootNode = tree.model.root as BrowserTreeNode
            tree.selectionPath = TreePath(arrayOf<Any>(rootNode, rootNode.firstChild))
        }
    }

    override fun valueChanged(selectionEvent: TreeSelectionEvent) {
        services.activate()

        val selectedPath = selectionEvent.path
        history.addHistoryEntry(selectedPath)
        showDetailPaneForPath(selectedPath)
    }

    private fun BrowserPath.addReferenceHolder(classMember: ClassMember) {
        addPathComponent(ReferenceHolder(classMember.name, classMember.descriptor))
    }

    private fun BrowserPath.addIndexHolder(node: BrowserTreeNode) {
        addPathComponent(IndexHolder(node.index))
    }

    private fun showDetailPaneForPath(path: TreePath) {
        val node = path.lastPathComponent as BrowserTreeNode
        val nodeType = node.type
        detailPane.showPane(nodeType, path)
    }
}
