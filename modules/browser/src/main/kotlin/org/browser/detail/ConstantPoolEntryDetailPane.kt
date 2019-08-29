/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.ConstantType
import org.gjt.jclasslib.structures.constants.ConstantPlaceholder
import java.awt.CardLayout
import java.awt.Color
import java.util.*
import javax.swing.JPanel
import javax.swing.tree.TreePath

class ConstantPoolEntryDetailPane(services: BrowserServices) : DetailPane<Constant>(Constant::class.java, services) {

    private val constantTypeToDetailPane: EnumMap<ConstantType, ConstantDetailPane<*>> = EnumMap(ConstantType::class.java)

    override fun setupComponent() {
        layout = CardLayout()

        add(JPanel().apply { background = Color.RED }, NAME_UNKNOWN)
        addCard(ConstantUtf8InfoDetailPane(services), ConstantType.UTF8)
        addCard(ConstantClassInfoDetailPane(services), ConstantType.CLASS)
        addCard(ConstantDoubleInfoDetailPane(services), ConstantType.DOUBLE)
        addCard(ConstantLongInfoDetailPane(services), ConstantType.LONG)
        addCard(ConstantFloatInfoDetailPane(services), ConstantType.FLOAT)
        addCard(ConstantIntegerInfoDetailPane(services), ConstantType.INTEGER)
        addCard(ConstantNameAndTypeInfoDetailPane(services), ConstantType.NAME_AND_TYPE)
        addCard(ConstantStringInfoDetailPane(services), ConstantType.STRING)
        addCard(ConstantReferenceDetailPane(services), ConstantType.FIELDREF)
        addCard(ConstantReferenceDetailPane(services), ConstantType.METHODREF)
        addCard(ConstantReferenceDetailPane(services), ConstantType.INTERFACE_METHODREF)
        addCard(ConstantDynamicDetailPane(services), ConstantType.INVOKE_DYNAMIC)
        addCard(ConstantMethodHandleInfoDetailPane(services), ConstantType.METHOD_HANDLE)
        addCard(ConstantMethodTypeDetailPane(services), ConstantType.METHOD_TYPE)
        addCard(ConstantModuleInfoDetailPane(services), ConstantType.MODULE)
        addCard(ConstantPackageInfoDetailPane(services), ConstantType.PACKAGE)
        addCard(ConstantDynamicDetailPane(services), ConstantType.DYNAMIC)
    }

    override fun show(treePath: TreePath) {
        val constant = getElement(treePath)
        if (constant == ConstantPlaceholder) {
            showCard(NAME_UNKNOWN)
        } else {
            val constantType = constant.constantType
            val detailPane = constantTypeToDetailPane[constantType]
            if (detailPane != null) {
                detailPane.show(treePath)
                showCard(constantType.name)
                return
            } else {
                showCard(NAME_UNKNOWN)

            }
        }
    }

    private fun addCard(detailPane: ConstantDetailPane<*>, constantType: ConstantType) {
        add(detailPane.displayComponent, constantType.name)
        constantTypeToDetailPane[constantType] = detailPane
    }

    private fun showCard(name: String) {
        (layout as CardLayout).show(this, name)
    }

    companion object {
        private const val NAME_UNKNOWN = "ConstantUnknown"
    }

}

