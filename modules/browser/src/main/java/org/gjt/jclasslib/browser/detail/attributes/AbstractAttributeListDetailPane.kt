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

import javax.swing.*
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel
import javax.swing.table.TableModel
import javax.swing.tree.TreePath
import java.util.WeakHashMap

//TODO remove
abstract class AbstractAttributeListDetailPane(services: BrowserServices) : ListDetailPane(services) {

    private val tableModel: AbstractAttributeTableModel
        get() = table.model as AbstractAttributeTableModel

    override fun getTableModel(treePath: TreePath): TableModel = getCachedTableModel(getAttribute(treePath))

    override fun link(row: Int, column: Int) {
        tableModel.link(row, column)
    }

    protected abstract fun createTableModel(attribute: AttributeInfo): AbstractAttributeTableModel

    protected fun getColumnWidth(column: Int): Int {
        return tableModel.getColumnWidth(column)
    }

    override fun createTableColumnModel(table: JTable) {

        val tableColumnModel: TableColumnModel? = tableModel.tableColumnModel
        if (tableColumnModel == null) {
            table.createDefaultColumnsFromModel()
            table.columnModel.let {
                tableModel.tableColumnModel = it
                adjustColumns(it)
            }
        } else {
            table.columnModel = tableColumnModel
        }
    }

    private fun adjustColumns(tableColumnModel: TableColumnModel) {
        val indexColumn = tableColumnModel.getColumn(0)
        indexColumn.maxWidth = indexColumn.width

        for (column in 0..tableColumnModel.columnCount - 1) {
            tableColumnModel.getColumn(column).apply {
                adjustColumn(this, column)
            }
        }
    }

    protected open fun adjustColumn(tableColumn: TableColumn, column: Int) {
        tableColumn.apply {
            minWidth = COLUMN_MIN_WIDTH
            val columnWidth = if (column == 0) ROW_NUMBER_COLUMN_WIDTH else getColumnWidth(column)
            width = columnWidth
            preferredWidth = columnWidth
        }
    }

    private fun getCachedTableModel(attribute: AttributeInfo): AbstractAttributeTableModel =
            attributeToTableModel.getOrPut(attribute) {
                createTableModel(attribute)
            }

    companion object {

        /** Default width in pixels for a column displaying a number.  */
        @JvmField
        protected val NUMBER_COLUMN_WIDTH = 60
        /** Default width in pixels for a column displaying a hyperlink.  */
        @JvmField
        protected val LINK_COLUMN_WIDTH = 80
        /** Default width in pixels for a column displaying a verbose entry.  */
        @JvmField
        protected val VERBOSE_COLUMN_WIDTH = 250

        private val COLUMN_MIN_WIDTH = 20
        private val ROW_NUMBER_COLUMN_WIDTH = 35

        private val attributeToTableModel = WeakHashMap<AttributeInfo, AbstractAttributeTableModel>()
    }

}

