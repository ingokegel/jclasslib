/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.window.*
import org.gjt.jclasslib.structures.ClassMember
import org.gjt.jclasslib.structures.FieldInfo
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.MethodInfo
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JSplitPane
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.TreePath

class BrowserComponent(private val services: BrowserServices) : JComponent(), TreeSelectionListener {
    val history: BrowserHistory = BrowserHistory(services)
    val detailPane: BrowserDetailPane = BrowserDetailPane(services)
    val treePane: BrowserTreePane = BrowserTreePane(services)

    private val splitPane: JSplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, detailPane).apply {
        isContinuousLayout = true
    }

    init {
        layout = BorderLayout()
        add(splitPane, BorderLayout.CENTER)
        treePane.tree.addTreeSelectionListener(this)
    }

    var browserPath: BrowserPath?
        get() {
            //TODO support general paths, not just methods and fields
            val selectionPath = treePane.tree.selectionPath
            if (selectionPath == null || selectionPath.pathCount < 3) {
                return null
            }
            val categoryNode = selectionPath.getPathComponent(2) as BrowserTreeNode
            if (categoryNode.type == NodeType.NO_CONTENT) {
                return null
            }
            return createBrowserPath(categoryNode, selectionPath)

        }
        set(browserPath) {
            if (browserPath == null) {
                return
            }
            val pathComponents = browserPath.pathComponents
            val it = pathComponents.iterator()
            if (!it.hasNext()) {
                return
            }
            val categoryComponent = it.next() as CategoryHolder
            val category = categoryComponent.category
            val initialCategoryPath: TreePath = treePane.getPathForCategory(category)
            val path = buildPath(initialCategoryPath, category, it)
            val pathObjects = path.path

            treePane.tree.apply {
                expandPath(path)
                selectionPath = path
                if (pathObjects.size > 2) {
                    val categoryPath = TreePath(arrayOf(pathObjects[0], pathObjects[1], pathObjects[2]))
                    scrollPathToVisible(categoryPath)
                }
            }
        }

    val title: String
        get() = services.classFile.simpleClassName

    private fun createBrowserPath(categoryNode: BrowserTreeNode, selectionPath: TreePath) = BrowserPath().apply {
        val category = categoryNode.type
        addPathComponent(CategoryHolder(category))
        when (category) {
            NodeType.METHOD -> {
                addClassMemberPathComponent(categoryNode.element as MethodInfo, this, selectionPath)
            }
            NodeType.FIELD -> {
                addClassMemberPathComponent(categoryNode.element as FieldInfo, this, selectionPath)
            }
            else -> {
                addPathComponent(IndexHolder(categoryNode.index))
            }
        }
    }

    private fun buildPath(path: TreePath, category: NodeType, it: MutableIterator<PathComponent>): TreePath {
        if (it.hasNext()) {
            val pathComponent = it.next()
            val childIndex: Int
            if (pathComponent is ReferenceHolder) {
                try {
                    if (category == NodeType.METHOD) {
                        childIndex = services.classFile.getMethodIndex(pathComponent.name, pathComponent.type)
                    } else if (category == NodeType.FIELD) {
                        childIndex = services.classFile.getFieldIndex(pathComponent.name, pathComponent.type)
                    } else {
                        return path
                    }
                } catch (ex: InvalidByteCodeException) {
                    return path
                }

            } else if (pathComponent is IndexHolder) {
                childIndex = pathComponent.index
            } else {
                return path
            }
            val lastNode = path.lastPathComponent as BrowserTreeNode
            if (childIndex >= lastNode.childCount) {
                return path
            }
            return buildPath(path.pathByAddingChild(lastNode.getChildAt(childIndex)), category, it)
        } else {
            return path
        }
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
        history.updateHistory(selectedPath)
        showDetailPaneForPath(selectedPath)
    }

    private fun addClassMemberPathComponent(classMember: ClassMember, browserPath: BrowserPath, selectionPath: TreePath) {
        try {
            browserPath.addPathComponent(ReferenceHolder(classMember.name, classMember.descriptor))
            if (selectionPath.pathCount > 3) {
                for (i in 3..selectionPath.pathCount - 1) {
                    val attributeNode = selectionPath.getPathComponent(i) as BrowserTreeNode
                    browserPath.addPathComponent(IndexHolder(attributeNode.index))
                }
            }
        } catch (ex: InvalidByteCodeException) {
        }
    }

    private fun showDetailPaneForPath(path: TreePath) {
        val node = path.lastPathComponent as BrowserTreeNode
        val nodeType = node.type
        detailPane.showPane(nodeType, path)
    }
}
