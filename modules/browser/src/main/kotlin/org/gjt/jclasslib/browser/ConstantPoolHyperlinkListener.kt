/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.tree.TreePath

class ConstantPoolHyperlinkListener(private val services: BrowserServices, private val constantPoolIndex: Int) : MouseAdapter() {

    override fun mouseClicked(event: MouseEvent?) {
        link(services, constantPoolIndex)
    }

    companion object {

        fun link(services: BrowserServices, constantPoolIndex: Int) {
            if (constantPoolIndex <= 0) {
                return
            }
            val newPath = linkPath(services, constantPoolIndex)
            services.browserComponent.treePane.tree.apply {
                selectionPath = newPath
                scrollPathToVisible(newPath)
            }
        }

        private fun linkPath(services: BrowserServices, constantPoolIndex: Int): TreePath {
            val constantPoolPath = services.browserComponent.treePane.getPathForCategory(NodeType.CONSTANT_POOL_ENTRY)
            val constantPoolNode = constantPoolPath.lastPathComponent as BrowserTreeNode
            val targetNode = constantPoolNode.getChildAt(constantPoolIndex - 1)
            return constantPoolPath.pathByAddingChild(targetNode)
        }
    }

}

