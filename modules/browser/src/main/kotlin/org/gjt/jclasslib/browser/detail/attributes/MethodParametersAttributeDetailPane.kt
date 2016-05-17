/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.MethodParametersAttribute
import org.gjt.jclasslib.structures.attributes.MethodParametersEntry
import java.util.*

class MethodParametersAttributeDetailPane(services: BrowserServices) : TableDetailPane<MethodParametersAttribute>(MethodParametersAttribute::class.java, services) {

    override fun createTableModel(attribute: MethodParametersAttribute) = AttributeTableModel(attribute.entries)

    override val rowHeightFactor: Float
        get() = 2f

    inner class AttributeTableModel(rows: Array<MethodParametersEntry>) : ColumnTableModel<MethodParametersEntry>(rows) {

        override fun buildColumns(columns: ArrayList<Column<MethodParametersEntry>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NamedConstantPoolLinkColumn<MethodParametersEntry>("Parameter Name", services, 200) {
                    override fun getConstantPoolIndex(row: MethodParametersEntry) = row.nameIndex
                })
                add(object : StringColumn<MethodParametersEntry>("Access Flags", 200) {
                    override fun createValue(row: MethodParametersEntry) = row.accessFlagsVerbose
                })
            }
        }
    }
}
