/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import java.util.*
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableColumnModel
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel

abstract class ColumnTableModel<T : AttributeInfo>(protected val attribute: T) : AbstractTableModel() {

    val columns = ArrayList<Column>().apply {
        buildColumns(this)
    }

    open protected fun buildColumns(columns: ArrayList<Column>) {
        columns.add(IndexColumn())
    }

    final override fun getColumnCount(): Int = columns.size
    final override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = columns[columnIndex].isEditable(rowIndex)
    final override fun getColumnName(columnIndex: Int): String = columns[columnIndex].name
    final override fun getColumnClass(columnIndex: Int): Class<*> = columns[columnIndex].columnClass
    final override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = columns[columnIndex].getValue(rowIndex)
    protected fun getColumnWidth(columnIndex: Int): Int = columns[columnIndex].width

    fun link(rowIndex: Int, columnIndex: Int) {
        columns[columnIndex].link(rowIndex)
    }
}
