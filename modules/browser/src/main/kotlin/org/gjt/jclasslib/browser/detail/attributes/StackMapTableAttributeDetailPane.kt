/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.StackMapFrameEntry
import org.gjt.jclasslib.structures.attributes.StackMapTableAttribute
import org.gjt.jclasslib.util.MultiLineHtmlCellHandler
import java.util.*
import javax.swing.JTable

class StackMapTableAttributeDetailPane(services: BrowserServices) : TableDetailPane<StackMapTableAttribute>(StackMapTableAttribute::class.java, services) {

    override fun createTableModel(attribute: StackMapTableAttribute) = AttributeTableModel(attribute.entries)

    override val isVariableRowHeight: Boolean
        get() = true

    override val autoResizeMode: Int
        get() = JTable.AUTO_RESIZE_LAST_COLUMN

    inner class AttributeTableModel(rows: Array<StackMapFrameEntry>) : ColumnTableModel<StackMapFrameEntry>(rows) {
        override fun buildColumns(columns: ArrayList<Column<StackMapFrameEntry>>) {
            super.buildColumns(columns)
            columns.add(object : StringColumn<StackMapFrameEntry>("Stack Map Frame", 600) {
                override fun createValue(row: StackMapFrameEntry): String = row.verbose
                override fun createTableCellRenderer() = createTableCellEditor()
                override fun createTableCellEditor() = MultiLineHtmlCellHandler { description ->
                    ConstantPoolHyperlinkListener.link(services, Integer.parseInt(description))
                }
                override fun isEditable(row: StackMapFrameEntry): Boolean = true
            })
        }
    }
}
