/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes

import java.util.*
import javax.swing.table.AbstractTableModel

abstract class ColumnTableModel<T : Any>(private val rows: Array<T>) : AbstractTableModel() {

    val columns = ArrayList<Column<T>>().apply {
        buildColumns(this)
    }

    protected open fun buildColumns(columns: ArrayList<Column<T>>) {
        columns.add(object : NumberColumn<T>("Nr.") {
            override fun createValue(row: T) = rows.indexOf(row)
            override val maxWidth: Int
                get() = width
        })
    }

    final override fun getRowCount(): Int = rows.size
    final override fun getColumnCount(): Int = columns.size
    final override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = columns[columnIndex].isEditable(rows[rowIndex])
    final override fun getColumnName(columnIndex: Int): String = columns[columnIndex].name
    final override fun getColumnClass(columnIndex: Int): Class<*> = columns[columnIndex].columnClass
    final override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = columns[columnIndex].getValue(rows[rowIndex])

    fun link(rowIndex: Int, columnIndex: Int) {
        columns[columnIndex].link(rows[rowIndex])
    }
}
