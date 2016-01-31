/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.attributes.MethodParametersAttribute
import org.gjt.jclasslib.structures.attributes.MethodParametersEntry
import java.util.*

class MethodParametersAttributeDetailPane(services: BrowserServices) : ColumnListDetailPane<MethodParametersAttribute>(services) {

    override fun createTableModel(attribute: MethodParametersAttribute) = AttributeTableModel(attribute)
    override val attributeClass: Class<MethodParametersAttribute>
        get() = MethodParametersAttribute::class.java

    override val rowHeightFactor: Float
        get() = 2f

    protected inner class AttributeTableModel(attribute: MethodParametersAttribute) : ColumnTableModel<MethodParametersAttribute>(attribute) {

        override fun buildColumns(columns: ArrayList<Column>) {
            super.buildColumns(columns)

            columns.apply {
                add(object : ConstantPoolLinkWithCommentColumn("Parameter Name", services, 200) {
                    override fun getConstantPoolIndex(rowIndex: Int) = entries[rowIndex].nameIndex
                })
                add(object : StringColumn("Access Flags", 200) {
                    override fun createValue(rowIndex: Int) = entries[rowIndex].accessFlagsVerbose
                })
            }
        }

        override fun getRowCount(): Int {
            return entries.size
        }
        private val entries: Array<MethodParametersEntry>
            get() = attribute.entries

    }
}
