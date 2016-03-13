/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.detail.AttributeDetailPane
import org.gjt.jclasslib.browser.detail.ConstantPoolDetailPane
import org.gjt.jclasslib.browser.detail.EmptyDetailPane
import java.awt.CardLayout
import java.awt.Dimension
import java.util.*
import javax.swing.JPanel
import javax.swing.tree.TreePath

class BrowserDetailPane(private val services: BrowserServices) : JPanel() {

    private val nodeTypeToDetailPane = HashMap<NodeType, DetailPane<*>>()
    var currentDetailPane: DetailPane<*> = EmptyDetailPane(services)
        private set

    init {
        layout = CardLayout()
        add(JPanel(), NodeType.NO_CONTENT.name)
        minimumSize = Dimension(150, 150)
        preferredSize = Dimension(150, 150)
    }

    fun showPane(nodeType: NodeType, treePath: TreePath) {
        currentDetailPane = getDetailPane(nodeType)
        currentDetailPane.show(treePath)
        showCard(nodeType)
    }

    private fun showCard(nodeType: NodeType) {
        (layout as CardLayout).show(this, nodeType.name)
    }

    val attributeDetailPane: AttributeDetailPane
        get() = getDetailPane(NodeType.ATTRIBUTE) as AttributeDetailPane

    val constantPoolDetailPane: ConstantPoolDetailPane
        get() = getDetailPane(NodeType.CONSTANT_POOL) as ConstantPoolDetailPane

    private fun getDetailPane(nodeType: NodeType): DetailPane<*> {
        return nodeTypeToDetailPane.getOrPut(nodeType) {
            nodeType.createDetailPanel(services).apply {
                this@BrowserDetailPane.add(displayComponent, nodeType.name)
            }
        }
    }
}
