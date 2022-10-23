/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.LineNumberTableAttribute
import org.gjt.jclasslib.structures.attributes.LineNumberTableEntry

class LineNumberTableAttributeDetailPane(services: BrowserServices) : TableDetailPane<LineNumberTableAttribute>(LineNumberTableAttribute::class.java, services) {

    override fun createTableModel(attribute: LineNumberTableAttribute) = AttributeTableModel(attribute.lineNumberTable)

    class AttributeTableModel(rows: Array<LineNumberTableEntry>) : ColumnTableModel<LineNumberTableEntry>(rows) {
        override fun buildColumns(columns: ArrayList<Column<LineNumberTableEntry>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NumberColumn<LineNumberTableEntry>(getString("column.start.pc")) {
                    override fun createValue(row: LineNumberTableEntry): Number = row.startPc
                })
                add(object : NumberColumn<LineNumberTableEntry>(getString("column.line.number"), 100) {
                    override fun createValue(row: LineNumberTableEntry): Number = row.lineNumber
                })
            }
        }
    }
}

