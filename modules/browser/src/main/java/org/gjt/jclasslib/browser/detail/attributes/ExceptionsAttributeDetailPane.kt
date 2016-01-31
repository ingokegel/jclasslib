/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.ExceptionsAttribute
import java.util.*

class ExceptionsAttributeDetailPane(services: BrowserServices) : TableDetailPane<ExceptionsAttribute>(services) {

    override fun createTableModel(attribute: ExceptionsAttribute) = AttributeTableModel(attribute)
    override val attributeClass: Class<ExceptionsAttribute>
        get() = ExceptionsAttribute::class.java

    protected inner class AttributeTableModel(attribute: ExceptionsAttribute) : ColumnTableModel<ExceptionsAttribute>(attribute) {
        override fun buildColumns(columns: ArrayList<Column>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : ConstantPoolLinkColumn("Exception", services) {
                    override fun getConstantPoolIndex(rowIndex: Int) = exceptionIndexTable[rowIndex]
                })

                add(object : StringColumn("Verbose") {
                    override fun createValue(rowIndex: Int) =
                            getConstantPoolEntryName(exceptionIndexTable[rowIndex])
                })
            }
        }

        override fun getRowCount() = exceptionIndexTable.size

        private val exceptionIndexTable: IntArray
            get() = attribute.exceptionIndexTable
    }
}
