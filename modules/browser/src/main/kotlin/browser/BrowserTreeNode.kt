/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.jetbrains.annotations.Nls
import javax.swing.tree.DefaultMutableTreeNode

open class BrowserTreeNode(@Nls text: String, val type: NodeType, val element: Any? = null) :
    DefaultMutableTreeNode(text), Iterable<BrowserTreeNode> {

    val index: Int
        get() = getParent().getIndex(this)

    val allChildren by lazy { iterator().asSequence().toList() }

    @Suppress("UNCHECKED_CAST")
    override fun iterator() = (children ?: emptyList<BrowserTreeNode>()).iterator() as Iterator<BrowserTreeNode>

    fun filterChildren(predicate: (node: BrowserTreeNode) -> Boolean) {
        val filteredChildren = allChildren.filter(predicate)
        removeAllChildren()
        filteredChildren.forEach {
            add(it)
        }
    }
}

class RefreshableBrowserTreeNode(type: NodeType, element: Any? = null, private val textProvider: () -> String) :
    BrowserTreeNode(textProvider(), type, element) {

    fun refresh() {
        userObject = textProvider()
    }
}