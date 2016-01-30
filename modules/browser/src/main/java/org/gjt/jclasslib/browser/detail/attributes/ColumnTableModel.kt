/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import java.util.*
import javax.swing.table.AbstractTableModel

abstract class ColumnTableModel(protected var attribute: AttributeInfo) : AbstractTableModel() {

    private val columns = ArrayList<Column>().apply {
        buildColumns(this)
    }

    protected fun buildColumns(columns: ArrayList<Column>) {
        columns.add(IndexColumn())
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = columns[columnIndex].isEditable(rowIndex)
    override fun getColumnName(columnIndex: Int): String = columns[columnIndex].name
    override fun getColumnClass(columnIndex: Int): Class<*> = columns[columnIndex].columnClass
    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = columns[columnIndex].getValue(rowIndex)
    protected fun getColumnWidth(columnIndex: Int): Int = columns[columnIndex].width

    fun link(rowIndex: Int, columnIndex: Int) {
        columns[columnIndex].link(rowIndex)
    }
}
