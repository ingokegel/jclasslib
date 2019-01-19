/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import java.util.*
import javax.swing.tree.DefaultMutableTreeNode

open class BrowserTreeNode(text: String, val type: NodeType, val element: Any? = null) : DefaultMutableTreeNode(text), Iterable<BrowserTreeNode> {
    val index: Int
        get() = getParent().getIndex(this)

    val originalChildren = ArrayList<BrowserTreeNode>()

    @Suppress("UNCHECKED_CAST")
    override fun iterator() = (children ?: emptyList<BrowserTreeNode>()).iterator() as Iterator<BrowserTreeNode>

    fun filterChildren(predicate: (node: BrowserTreeNode) -> Boolean) {
        if (originalChildren.isEmpty()) {
            originalChildren.addAll(iterator().asSequence())
        }
        removeAllChildren()
        originalChildren.filter(predicate).forEach {
            add(it)
        }
    }
}
