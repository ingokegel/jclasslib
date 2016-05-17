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

class ExceptionsAttributeDetailPane(services: BrowserServices) : TableDetailPane<ExceptionsAttribute>(ExceptionsAttribute::class.java, services) {

    override fun createTableModel(attribute: ExceptionsAttribute) = AttributeTableModel(attribute.exceptionIndexTable)

    inner class AttributeTableModel(rows: IntArray) : ColumnTableModel<Int>(rows.toTypedArray()) {
        override fun buildColumns(columns: ArrayList<Column<Int>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : ConstantPoolLinkColumn<Int>("Exception", services) {
                    override fun getConstantPoolIndex(row: Int) = row
                })
                add(object : StringColumn<Int>("Verbose") {
                    override fun createValue(row: Int) = getConstantPoolEntryName(row)
                })
            }
        }
    }
}
