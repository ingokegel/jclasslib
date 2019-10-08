/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util

import java.awt.Color
import java.awt.Component
import java.awt.Rectangle
import javax.swing.JTable
import javax.swing.UIManager
import javax.swing.border.EmptyBorder
import javax.swing.table.TableCellRenderer

class ExtendedTableCellRenderer : ExtendedJLabel(""), TableCellRenderer {

    private var unselectedForeground: Color? = null
    private var unselectedBackground: Color? = null

    init {
        isOpaque = true
        border = NO_FOCUS_BORDER
    }

    override fun setForeground(c: Color?) {
        super.setForeground(c)
        unselectedForeground = c
    }

    override fun setBackground(c: Color?) {
        super.setBackground(c)
        unselectedBackground = c
    }

    override fun updateUI() {
        super.updateUI()
        foreground = null
        background = null
    }

    override fun getTableCellRendererComponent(table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        if (isSelected) {
            super.setForeground(table.selectionForeground)
            super.setBackground(table.selectionBackground)

        } else {
            super.setForeground(if (unselectedForeground != null) unselectedForeground else table.foreground)
            super.setBackground(if (unselectedBackground != null) unselectedBackground else table.background)
        }

        font = table.font

        if (hasFocus) {
            border = UIManager.getBorder("Table.focusCellHighlightBorder")
            if (table.isCellEditable(row, column)) {
                super.setForeground(UIManager.getColor("Table.focusCellForeground"))
                super.setBackground(UIManager.getColor("Table.focusCellBackground"))
            }
        } else {
            border = NO_FOCUS_BORDER
        }

        setValue(value)

        val colorMatch = background == table.background && table.isOpaque
        isOpaque = !colorMatch

        return this
    }

    override fun validate() {
    }

    override fun revalidate() {
    }

    override fun repaint(tm: Long, x: Int, y: Int, width: Int, height: Int) {
    }

    override fun repaint(r: Rectangle) {
    }

    override fun firePropertyChange(propertyName: String, oldValue: Any?, newValue: Any?) {
        if (propertyName == "text") {
            super.firePropertyChange(propertyName, oldValue, newValue)
        }
    }

    override fun firePropertyChange(propertyName: String, oldValue: Boolean, newValue: Boolean) {
    }

    private fun setValue(value: Any?) {
        text = value?.toString() ?: ""
    }

    companion object {
        private val NO_FOCUS_BORDER = EmptyBorder(1, 1, 1, 1)
    }


}
