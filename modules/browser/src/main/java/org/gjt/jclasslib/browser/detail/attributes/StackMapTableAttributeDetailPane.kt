/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.attributes.ExceptionsAttribute
import org.gjt.jclasslib.structures.attributes.StackMapFrameEntry
import org.gjt.jclasslib.structures.attributes.StackMapTableAttribute
import org.gjt.jclasslib.util.MultiLineHtmlCellHandler
import java.util.*

import javax.swing.*
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn

class StackMapTableAttributeDetailPane(services: BrowserServices)  : ColumnListDetailPane<StackMapTableAttribute>(services) {

    override fun createTableModel(attribute: StackMapTableAttribute) : ColumnTableModel<StackMapTableAttribute> = AttributeTableModel(attribute)
    override val attributeClass: Class<StackMapTableAttribute>
        get() = StackMapTableAttribute::class.java

    override val isVariableRowHeight: Boolean
        get() = true

    override val autoResizeMode: Int
        get() = JTable.AUTO_RESIZE_LAST_COLUMN

    private inner class AttributeTableModel(attribute: StackMapTableAttribute) : ColumnTableModel<StackMapTableAttribute>(attribute) {
        override fun buildColumns(columns: ArrayList<Column>) {
            super.buildColumns(columns)
            columns.add(object : StringColumn("Stack Map Frame", 600) {
                override fun createValue(rowIndex: Int): String = entries[rowIndex].verbose
                override fun createTableCellRenderer() = createTableCellEditor()
                override fun createTableCellEditor() = MultiLineHtmlCellHandler() {description ->
                    ConstantPoolHyperlinkListener.link(services, Integer.parseInt(description))
                }
                override fun isEditable(rowIndex: Int): Boolean {
                    return true
                }
            })
        }

        private val entries: Array<StackMapFrameEntry>
            get() = attribute.entries

        override fun getRowCount(): Int = entries.size
    }
}
