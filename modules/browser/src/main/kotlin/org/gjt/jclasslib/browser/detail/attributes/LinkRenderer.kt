/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.util.ExtendedTableCellRenderer
import org.gjt.jclasslib.util.getLinkColor
import java.awt.Color
import java.awt.Component
import java.awt.Point
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

class LinkRenderer : TableCellRenderer {

    private val linkLineRenderer = ExtendedTableCellRenderer()
    private val infoLineRenderer = ExtendedTableCellRenderer()
    private val standardForeground: Color? = linkLineRenderer.foreground
    private val panel = JPanel(MigLayout("insets 0, gapy 0, wrap")).apply {
        add(linkLineRenderer)
        add(infoLineRenderer)
    }

    override fun getTableCellRendererComponent(table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        linkLineRenderer.apply {
            if (value == null || value.toString() == DetailPane.CPINFO_LINK_TEXT + "0") {
                foreground = standardForeground
                isUnderlined = false
            } else {
                foreground = getLinkColor()
                isUnderlined = true
            }
            getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        }

        if (value is LinkWithComment) {
            infoLineRenderer.getTableCellRendererComponent(table, value.commentValue, isSelected, false, row, column)
            infoLineRenderer.isVisible = true
        } else {
            infoLineRenderer.isVisible = false
        }

        return panel.apply {
            background = linkLineRenderer.background
            border = TableDetailPane.noFocusBorder
            linkLineRenderer.border = null
        }
    }

    fun isLinkLabelHit(point: Point): Boolean = linkLineRenderer.bounds.contains(point)
}
