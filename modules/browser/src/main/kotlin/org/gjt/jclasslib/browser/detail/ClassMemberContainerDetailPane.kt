/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.BrowserTreeNode
import org.gjt.jclasslib.structures.AccessFlag
import org.gjt.jclasslib.structures.ClassMember
import java.awt.GridBagConstraints
import javax.swing.JTree
import javax.swing.tree.TreePath

class ClassMemberContainerDetailPane(services: BrowserServices, signatureMode: FixedListWithSignatureDetailPane.SignatureMode) :
        FixedListWithSignatureDetailPane<Array<out ClassMember>>(Array<out ClassMember>::class.java, services, signatureMode) {

    val filterPane = object : FilterPane<AccessFlag, ClassMember>(this@ClassMemberContainerDetailPane) {
        override fun getAllFilterKeys() = signatureMode.getAccessFlags()
        override fun isElementTextFiltered(element: ClassMember, filterText: String) = isShowAll || element.name.contains(filterText)
        override fun getFilterKeys(element: ClassMember) = AccessFlag.decompose(element.accessFlags)
    }

    override fun show(treePath: TreePath) {
        super.show(treePath)
        updateFilter()
    }

    override fun updateFilter(tree: JTree, treeNode: BrowserTreeNode) {
        super.updateFilter(tree, treeNode)
        filterPane.updateFilterCheckboxes(getElement(TreePath(treeNode.path)).toList())
    }

    override fun isChildShown(node: BrowserTreeNode) = filterPane.isElementShown(node.element as ClassMember)

    override val signatureVerbose: String
        get() = StringBuilder().apply {
            for (classMember in element?.filter { filterPane.isElementShown(it) } ?: listOf()) {
                appendSignature(classMember, signatureMode)
                append('\n')
            }

        }.toString()

    override fun addLabels() {
        add(filterPane, gc() {
            gridx = 0
            gridwidth = GridBagConstraints.REMAINDER
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })
        nextLine()

        //TODO add separator
        addDetail("Member count:") { classMembers -> classMembers.count { filterPane.isElementShown(it) }.toString() }
        super.addLabels()
    }

    override val signatureButtonText: String
        get() = "Copy signatures to clipboard"
}

