/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.ListDetailPane
import org.gjt.jclasslib.structures.AttributeInfo
import java.util.*
import javax.swing.JTable
import javax.swing.table.TableModel
import javax.swing.tree.TreePath

abstract class ColumnListDetailPane<T : AttributeInfo>(services: BrowserServices) : ListDetailPane(services) {

    private val tableModel: ColumnTableModel<*>
        get() = table.model as ColumnTableModel<*>

    private val attributeToTableModel = WeakHashMap<AttributeInfo, ColumnTableModel<T>>()

    override fun getTableModel(treePath: TreePath): TableModel = getCachedTableModel(attributeClass.cast(getAttribute(treePath)))

    override fun link(row: Int, column: Int) {
        tableModel.link(row, column)
    }

    protected abstract fun createTableModel(attribute: T): ColumnTableModel<T>
    protected abstract val attributeClass: Class<T>

    override fun createTableColumnModel(table: JTable) {
        super.createTableColumnModel(table)
        table.columnModel.columns.iterator().withIndex().forEach {
            val column = tableModel.columns[it.index]
            it.value.apply {
                minWidth = column.minWidth
                maxWidth = column.maxWidth
                width = column.width
                preferredWidth = column.width
                cellRenderer = column.createTableCellRenderer()
                cellEditor = column.createTableCellEditor()
            }
        }
    }

    private fun getCachedTableModel(attribute: T): ColumnTableModel<T> =
            attributeToTableModel.getOrPut(attribute) {
                createTableModel(attribute)
            }
}

