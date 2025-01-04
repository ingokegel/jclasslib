/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.browser.detail.attributes.Column
import org.gjt.jclasslib.browser.detail.attributes.ColumnTableModel
import org.gjt.jclasslib.browser.detail.attributes.NamedConstantPoolLinkColumn
import org.gjt.jclasslib.browser.detail.attributes.NumberColumn
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.structures.attributes.ExceptionTableEntry

class ExceptionTableDetailPane(services: BrowserServices) : TableDetailPane<CodeAttribute>(CodeAttribute::class.java, services) {

    init {
        name = getString("code.tab.exception.table")
    }

    override fun createTableModel(attribute: CodeAttribute) = AttributeTableModel(attribute.exceptionTable)

    override val rowHeightFactor: Float
        get() = 2f

    inner class AttributeTableModel(rows: Array<ExceptionTableEntry>) : ColumnTableModel<ExceptionTableEntry>(rows) {

        override fun buildColumns(columns: ArrayList<Column<ExceptionTableEntry>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NumberColumn<ExceptionTableEntry>(getString("column.start.pc")) {
                    override fun createValue(row: ExceptionTableEntry) = row.startPc
                })
                add(object : NumberColumn<ExceptionTableEntry>(getString("column.end.pc")) {
                    override fun createValue(row: ExceptionTableEntry) = row.endPc
                })
                add(object : NumberColumn<ExceptionTableEntry>(getString("column.handler.pc"), 70) {
                    override fun createValue(row: ExceptionTableEntry) = row.handlerPc
                })
                add(object : NamedConstantPoolLinkColumn<ExceptionTableEntry>(getString("column.catch.type"), services, 250) {
                    override fun getConstantPoolIndex(row: ExceptionTableEntry) = row.catchType
                    override fun getComment(constantPoolIndex: Int) = if (constantPoolIndex == 0) {
                        "any"
                    } else {
                        super.getComment(constantPoolIndex)
                    }
                })
            }
        }
    }
}
