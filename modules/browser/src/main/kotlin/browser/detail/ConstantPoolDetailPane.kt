/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.BrowserTreeNode
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.structures.AbstractConstant
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.ConstantType
import org.gjt.jclasslib.structures.constants.ConstantPlaceholder
import javax.swing.JTree
import javax.swing.tree.TreePath

class ConstantPoolDetailPane(services: BrowserServices) : DetailPane<Array<Constant>>(Array<Constant>::class.java, services) {

    val filterPane = object : FilterPane<ConstantType, Constant>(this@ConstantPoolDetailPane) {
        override fun getAllFilterKeys() = ConstantType.values().toList()
        override fun isElementTextFiltered(element: Constant, filterText: String) = element is AbstractConstant && (isShowAll || element.verbose.contains(filterText))
        override fun getFilterKeys(element: Constant) = if (element == ConstantPlaceholder) setOf() else setOf(element.constantType)
    }

    override fun show(treePath: TreePath) {
        updateFilter(expand = false)
    }

    override fun updateFilter(tree: JTree, treeNode: BrowserTreeNode, expand: Boolean) {
        super.updateFilter(tree, treeNode, expand)
        filterPane.updateFilterCheckboxes(getElement(TreePath(treeNode.path)).toList())
    }

    override fun isChildShown(node: BrowserTreeNode) = filterPane.isElementShown(node.element as Constant)

    override fun setupComponent() {
        layout = MigLayout("wrap", "[grow]")
        add(filterPane, "growx")
    }

}
