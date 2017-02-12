/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import java.awt.Component
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.util.*
import javax.swing.JTable
import javax.swing.SwingUtilities
import javax.swing.border.EmptyBorder
import javax.swing.event.CellEditorListener
import javax.swing.event.ChangeEvent
import javax.swing.event.HyperlinkEvent
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class MultiLineHtmlCellHandler(val linkHandler: (description: String) -> Unit = {}) : HtmlDisplayTextArea(), TableCellRenderer, TableCellEditor {

    private val listeners = ArrayList<CellEditorListener>()
    private var table: JTable? = null

    init {
        isOpaque = true

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                select()
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent?) {
                table?.let { table ->
                    // Implement single selection dragging while dragging
                    val tableEvent = SwingUtilities.convertMouseEvent(this@MultiLineHtmlCellHandler, e, table)
                    val point = tableEvent.point
                    val row = getRowAtPoint(point, table)
                    table.selectionModel.setSelectionInterval(row, row)
                    if (row == table.editingRow) {
                        switchToSelected(table)
                    } else {
                        switchToUnselected(table)
                    }
                }
            }
        })

        addHyperlinkListener { e ->
            if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                linkHandler.invoke(e.description)
            }
        }
    }

    private fun getRowAtPoint(point: Point, table: JTable): Int {
        val row = table.rowAtPoint(point)
        if (row == -1) {
            val lastRow = table.rowCount - 1
            val lastCellRect = table.getCellRect(lastRow, 0, true)
            if (point.y > lastCellRect.y + lastCellRect.height) {
                return lastRow
            }
        }
        return row
    }

    private fun select() {
        table?.let { table ->
            val row = table.editingRow
            table.selectionModel.setSelectionInterval(row, row)
            switchToSelected(table)
        }
    }

    override fun getTableCellRendererComponent(table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        if (isSelected) {
            switchToSelected(table)
        } else {
            switchToUnselected(table)
        }
        font = table.font
        border = EmptyBorder(1, 1, 1, 1)
        text = value?.toString() ?: ""
        return this
    }

    private fun switchToUnselected(table: JTable) {
        foreground = table.foreground
        background = table.background
        inverted = false
    }

    private fun switchToSelected(table: JTable) {
        foreground = table.selectionForeground
        background = table.selectionBackground
        inverted = table.selectionForeground != table.foreground
    }

    override fun getTableCellEditorComponent(table: JTable, value: Any, isSelected: Boolean, row: Int, column: Int): Component {
        this.table = table
        return getTableCellRendererComponent(table, value, isSelected, false, row, column)
    }

    override fun getCellEditorValue() = null

    override fun isCellEditable(anEvent: EventObject?) = true

    override fun shouldSelectCell(eventObject: EventObject?) = false

    override fun stopCellEditing(): Boolean {
        val event = ChangeEvent(this)
        for (listener in ArrayList(listeners)) {
            listener.editingStopped(event)
        }
        return true
    }

    override fun cancelCellEditing() {
        val event = ChangeEvent(this)
        for (listener in ArrayList(listeners)) {
            listener.editingCanceled(event)
        }
    }

    override fun addCellEditorListener(l: CellEditorListener) {
        listeners.add(l)
    }

    override fun removeCellEditorListener(l: CellEditorListener) {
        listeners.remove(l)
    }
}