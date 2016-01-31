/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.LineNumberTableAttribute
import org.gjt.jclasslib.structures.attributes.LineNumberTableEntry
import java.util.*

class LineNumberTableAttributeDetailPane(services: BrowserServices) : TableDetailPane<LineNumberTableAttribute>(services) {

    override fun createTableModel(attribute: LineNumberTableAttribute) = AttributeTableModel(attribute)
    override val attributeClass: Class<LineNumberTableAttribute>
        get() = LineNumberTableAttribute::class.java

    protected inner class AttributeTableModel(attribute: LineNumberTableAttribute) : ColumnTableModel<LineNumberTableAttribute>(attribute) {
        override fun buildColumns(columns: ArrayList<Column>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NumberColumn("Start PC") {
                    override fun createValue(rowIndex: Int): Number = lineNumberTable[rowIndex].startPc
                })
            }
            columns.apply {
                add(object : NumberColumn("Line Number", 100) {
                    override fun createValue(rowIndex: Int): Number = lineNumberTable[rowIndex].lineNumber
                })
            }
        }

        override fun getRowCount(): Int {
            return lineNumberTable.size
        }

        private val lineNumberTable: Array<LineNumberTableEntry>
            get() = attribute.lineNumberTable
    }
}

