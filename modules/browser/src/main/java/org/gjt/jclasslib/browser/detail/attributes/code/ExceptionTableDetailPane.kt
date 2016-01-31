/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.attributes.*
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.structures.attributes.ExceptionTableEntry
import java.util.*

class ExceptionTableDetailPane(services: BrowserServices) : ColumnListDetailPane<CodeAttribute>(services) {

    init {
        name = "Exception table"
    }

    override fun createTableModel(attribute: CodeAttribute) = AttributeTableModel(attribute)
    override val attributeClass: Class<CodeAttribute>
        get() = CodeAttribute::class.java

    override val rowHeightFactor: Float
        get() = 2f

    protected inner class AttributeTableModel(attribute: CodeAttribute) : ColumnTableModel<CodeAttribute>(attribute) {

        override fun buildColumns(columns: ArrayList<Column>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NumberColumn("Start PC") {
                    override fun createValue(rowIndex: Int) = exceptionTable[rowIndex].startPc
                })
                add(object : NumberColumn("End PC") {
                    override fun createValue(rowIndex: Int) = exceptionTable[rowIndex].endPc
                })
                add(object : NumberColumn("Handler PC", 70) {
                    override fun createValue(rowIndex: Int) = exceptionTable[rowIndex].handlerPc
                })
                add(object : ConstantPoolLinkWithCommentColumn("Catch Type", services, 250) {
                    override fun getConstantPoolIndex(rowIndex: Int) = exceptionTable[rowIndex].catchType
                    override fun getComment(constantPoolIndex: Int) = if (constantPoolIndex == 0) {
                        "any"
                    } else {
                        super.getComment(constantPoolIndex)
                    }
                })
            }
        }

        override fun getRowCount(): Int {
            return exceptionTable.size
        }

        private val exceptionTable: Array<ExceptionTableEntry>
            get() = attribute.exceptionTable
    }
}
