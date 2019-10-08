/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.browser.detail.attributes.Column
import org.gjt.jclasslib.browser.detail.attributes.ColumnTableModel
import org.gjt.jclasslib.browser.detail.attributes.NamedConstantPoolLinkColumn
import org.gjt.jclasslib.browser.detail.attributes.NumberColumn
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.structures.attributes.ExceptionTableEntry
import java.util.*

class ExceptionTableDetailPane(services: BrowserServices) : TableDetailPane<CodeAttribute>(CodeAttribute::class.java, services) {

    init {
        name = "Exception table"
    }

    override fun createTableModel(attribute: CodeAttribute) = AttributeTableModel(attribute.exceptionTable)

    override val rowHeightFactor: Float
        get() = 2f

    inner class AttributeTableModel(rows: Array<ExceptionTableEntry>) : ColumnTableModel<ExceptionTableEntry>(rows) {

        override fun buildColumns(columns: ArrayList<Column<ExceptionTableEntry>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NumberColumn<ExceptionTableEntry>("Start PC") {
                    override fun createValue(row: ExceptionTableEntry) = row.startPc
                })
                add(object : NumberColumn<ExceptionTableEntry>("End PC") {
                    override fun createValue(row: ExceptionTableEntry) = row.endPc
                })
                add(object : NumberColumn<ExceptionTableEntry>("Handler PC", 70) {
                    override fun createValue(row: ExceptionTableEntry) = row.handlerPc
                })
                add(object : NamedConstantPoolLinkColumn<ExceptionTableEntry>("Catch Type", services, 250) {
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
