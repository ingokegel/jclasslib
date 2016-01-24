/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.AbstractDetailPane
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.BrowserTreeNode
import org.gjt.jclasslib.browser.detail.constants.*
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.ConstantType
import java.awt.CardLayout
import java.awt.Color
import java.util.*
import javax.swing.JPanel
import javax.swing.tree.TreePath

class ConstantPoolDetailPane(services: BrowserServices) : AbstractDetailPane(services) {

    private val constantTypeToDetailPane: EnumMap<ConstantType, AbstractDetailPane> = EnumMap(ConstantType::class.java)

    override fun setupComponent() {
        layout = CardLayout()

        add(JPanel().apply { background = Color.RED }, NAME_UNKNOWN)
        addCard(ConstantUtf8InfoDetailPane(browserServices), ConstantType.UTF8)
        addCard(ConstantClassInfoDetailPane(browserServices), ConstantType.CLASS)
        addCard(ConstantDoubleInfoDetailPane(browserServices), ConstantType.DOUBLE)
        addCard(ConstantLongInfoDetailPane(browserServices), ConstantType.LONG)
        addCard(ConstantFloatInfoDetailPane(browserServices), ConstantType.FLOAT)
        addCard(ConstantIntegerInfoDetailPane(browserServices), ConstantType.INTEGER)
        addCard(ConstantNameAndTypeInfoDetailPane(browserServices), ConstantType.NAME_AND_TYPE)
        addCard(ConstantStringInfoDetailPane(browserServices), ConstantType.STRING)
        addCard(ConstantReferenceDetailPane(browserServices), ConstantType.FIELDREF)
        addCard(ConstantReferenceDetailPane(browserServices), ConstantType.METHODREF)
        addCard(ConstantReferenceDetailPane(browserServices), ConstantType.INTERFACE_METHODREF)
        addCard(ConstantInvokeDynamicInfoDetailPane(browserServices), ConstantType.INVOKE_DYNAMIC)
        addCard(ConstantMethodHandleInfoDetailPane(browserServices), ConstantType.METHOD_HANDLE)
        addCard(ConstantMethodTypeDetailPane(browserServices), ConstantType.METHOD_TYPE)
    }

    override fun show(treePath: TreePath) {
        val constantPoolEntry = (treePath.lastPathComponent as BrowserTreeNode).element as Constant?
        if (constantPoolEntry != null) {
            val constantType = constantPoolEntry.constantType
            val detailPane = constantTypeToDetailPane[constantType]
            if (detailPane != null) {
                detailPane.show(treePath)
                showCard(constantType.name)
                return
            } else {
                showCard(NAME_UNKNOWN)

            }
        } else {
            showCard(NAME_UNKNOWN)
        }
    }

    private fun addCard(detailPane: AbstractDetailPane, constantType: ConstantType) {
        add(detailPane.displayComponent, constantType.name)
        constantTypeToDetailPane.put(constantType, detailPane)
    }

    private fun showCard(name: String) {
        (layout as CardLayout).show(this, name)
    }

    companion object {
        private val NAME_UNKNOWN = "ConstantUnknown"
    }

}

