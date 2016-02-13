/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.browser.detail.attributes.ColumnTableModel
import org.gjt.jclasslib.browser.detail.attributes.Link
import org.gjt.jclasslib.browser.detail.attributes.LinkRenderer
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.util.LinkMouseListener
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.util.*
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ListSelectionEvent
import javax.swing.event.TableColumnModelEvent
import javax.swing.event.TableColumnModelListener
import javax.swing.table.TableModel
import javax.swing.tree.TreePath

abstract class TableDetailPane<T : AttributeInfo>(elementClass: Class<T>, services: BrowserServices) : DetailPane<T>(elementClass, services) {

    protected val table: JTable = JTable().apply {
        this.autoResizeMode = JTable.AUTO_RESIZE_OFF
        selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        val rowHeightFactor = rowHeightFactor
        if (rowHeightFactor != 1f) {
            rowHeight = (rowHeight * rowHeightFactor).toInt()
        }

        TableLinkListener(this)
        gridColor = UIManager.getColor("control")
        this.autoResizeMode = autoResizeMode

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                val col = columnAtPoint(e.point)
                val row = rowAtPoint(e.point)
                if (col >= 0 && model.isCellEditable(row, col)) {
                    editCellAt(row, col)
                }
            }
        })

        if (isVariableRowHeight) {
            columnModel.addColumnModelListener(object : TableColumnModelListener {
                override fun columnAdded(e: TableColumnModelEvent) {
                }

                override fun columnRemoved(e: TableColumnModelEvent) {
                }

                override fun columnMoved(e: TableColumnModelEvent) {
                }

                override fun columnMarginChanged(e: ChangeEvent) {
                    updateRowHeights()
                }

                override fun columnSelectionChanged(e: ListSelectionEvent) {
                }
            })
        }
    }

    private val tableModel: ColumnTableModel<*>
        get() = table.model as ColumnTableModel<*>

    private val attributeToTableModel = WeakHashMap<T, ColumnTableModel<*>>()

    protected abstract fun createTableModel(attribute: T): ColumnTableModel<*>

    fun getTableModel(treePath: TreePath): TableModel = getCachedTableModel(getElement(treePath))

    fun link(row: Int, column: Int) {
        tableModel.link(row, column)
    }

    fun createTableColumnModel(table: JTable) {
        table.apply {
            createDefaultColumnsFromModel()
            columnModel.columns.iterator().withIndex().forEach {
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
    }

    private fun getCachedTableModel(attribute: T): ColumnTableModel<*> =
            attributeToTableModel.getOrPut(attribute) {
                createTableModel(attribute)
            }

    open protected val autoResizeMode: Int
        get() = JTable.AUTO_RESIZE_OFF

    open protected val isVariableRowHeight: Boolean
        get() = false

    override fun setupComponent() {
        layout = BorderLayout()
        add(JScrollPane(table).apply {
            viewport.background = Color.WHITE
        }, BorderLayout.CENTER)
    }

    override fun show(treePath: TreePath) {
        val tableModel = getTableModel(treePath)
        table.apply {
            model = tableModel
            createTableColumnModel(this)
            alignTop(Number::class.java)
            alignTop(String::class.java)
            setDefaultRenderer(Link::class.java, LinkRenderer())
        }
        if (isVariableRowHeight) {
            updateRowHeights()
        }
    }

    private fun JTable.alignTop(columnClass: Class<*>) {
        (getDefaultRenderer(columnClass) as JLabel).verticalAlignment = JLabel.TOP
    }

    private fun updateRowHeights() {
        for (row in 0..table.rowCount - 1) {
            val rowHeight = (0..table.columnCount - 1).map { column ->
                val c = table.prepareRenderer(table.getCellRenderer(row, column), row, column) as JComponent
                c.size = c.preferredSize.apply {
                    width = table.columnModel.getColumn(column).width
                }
                c.preferredSize.height
            }.max() ?: table.rowHeight
            table.setRowHeight(row, rowHeight)
        }
    }

    open protected val rowHeightFactor: Float
        get() = 1f

    fun selectIndex(index: Int) {
        if (index !in (0..table.rowCount)) {
            throw IllegalArgumentException("Invalid index: " + index)
        }
        table.selectionModel.setSelectionInterval(index, index)
    }

    private inner class TableLinkListener(component: JComponent) : LinkMouseListener(component) {
        override fun isLink(point: Point): Boolean {
            val column = table.columnAtPoint(point)
            val row = table.rowAtPoint(point)
            return row >= 0 && column >= 0 &&
                    table.getColumnClass(column) == Link::class.java &&
                    table.model.getValueAt(row, column).toString() != DetailPane.CPINFO_LINK_TEXT + "0" &&
                    isLinkLabelHit(point, row, column)
        }

        override fun link(point: Point) {
            link(table.rowAtPoint(point), table.columnAtPoint(point))
        }

        private fun isLinkLabelHit(point: Point, row: Int, column: Int): Boolean {
            val renderer = table.getCellRenderer(row, column) as LinkRenderer
            renderer.getTableCellRendererComponent(table, table.model.getValueAt(row, column), false, false, row, column)
            val cellRect = table.getCellRect(row, column, false)
            val translatedPoint = Point(point.x - cellRect.x, point.y - cellRect.y)
            return renderer.isLinkLabelHit(translatedPoint)
        }
    }
}

