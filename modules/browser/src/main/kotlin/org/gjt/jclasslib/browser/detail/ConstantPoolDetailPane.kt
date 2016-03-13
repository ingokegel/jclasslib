/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.BrowserTreeNode
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.structures.AbstractConstant
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.ConstantType
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JTree
import javax.swing.tree.TreePath

class ConstantPoolDetailPane(services: BrowserServices) : DetailPane<Array<Constant>>(Array<Constant>::class.java, services) {

    val filterCheckboxes = ConstantType.values().associate {
        it to JCheckBox(it.verbose).apply {
            addActionListener {
                updateFilter()
            }
        }
    }

    val filterPane = object : FilterPane(this@ConstantPoolDetailPane) {
        override fun addComponents() {
            super.addComponents()
            filterCheckboxes.values.forEachIndexed { i, checkBox ->
                add(filterComponent(checkBox), if (i % 2 == 0) "split, sgx col1, gapright para, $RADIO_BUTTON_INSET" else "sgx col2, wrap")
            }
            add(filterComponent(JButton("Toggle all").apply {
                addActionListener {
                    toggleCheckboxes(!filterCheckboxes.values.all { it.isSelected })
                }
            }), "newline unrel, $RADIO_BUTTON_INSET")
        }

        override fun textFilterEntered() {
            if (filterCheckboxes.values.none { it.isSelected }) {
                toggleCheckboxes(true)
            }
        }

        private fun toggleCheckboxes(selected: Boolean) {
            filterCheckboxes.values.forEach { it.isSelected = selected }
            updateFilter()
        }
    }

    override fun show(treePath: TreePath) {
        updateFilter()
    }

    override fun updateFilter(tree: JTree, treeNode: BrowserTreeNode) {
        super.updateFilter(tree, treeNode)
        val constantPool = getElement(TreePath(treeNode.path))
        val statistics = constantPool.filter { it is AbstractConstant && isConstantTextFiltered(it) }.
                groupBy { it.constantType }.mapValues { it.value.size }
        for ((constantType, checkBox) in filterCheckboxes) {
            filterCheckboxes[constantType]?.apply {
                text = "${constantType.verbose} (${statistics[constantType] ?: 0})"
            }
        }
    }

    override fun setupComponent() {
        layout = BorderLayout()
        add(filterPane, BorderLayout.NORTH)
    }

    override fun isChildShown(node: BrowserTreeNode) = isConstantShown(node.element as Constant)

    private fun isConstantShown(constant: Constant): Boolean {
        return filterPane.isShowAll || (
                filterCheckboxes.any { it.key == constant.constantType && it.value.isSelected } &&
                        isConstantTextFiltered(constant)
                )
    }

    private fun isConstantTextFiltered(constant: Constant): Boolean {
        return filterPane.isShowAll || constant.verbose.contains(filterPane.filterText)
    }

}
