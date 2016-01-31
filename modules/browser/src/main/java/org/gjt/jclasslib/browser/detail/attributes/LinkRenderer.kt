/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.AbstractDetailPane
import org.gjt.jclasslib.util.ExtendedTableCellRenderer
import org.gjt.jclasslib.util.HtmlDisplayTextArea
import java.awt.*
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

class LinkRenderer : TableCellRenderer {

    private val linkLineRenderer = ExtendedTableCellRenderer()
    private val infoLineRenderer = ExtendedTableCellRenderer()
    private val standardForeground: Color? = linkLineRenderer.foreground
    private val panel = JPanel(GridBagLayout()).apply {
        val gc = GridBagConstraints().apply {
            anchor = GridBagConstraints.NORTHWEST
            gridx = 0
        }
        add(linkLineRenderer, gc)
        add(infoLineRenderer, gc)
        add(JPanel().apply { isOpaque = false }, GridBagConstraints().apply {
            gridx = 0
            weighty = 1.0
            weightx = 1.0
            fill = GridBagConstraints.BOTH
        })
    }

    override fun getTableCellRendererComponent(table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        linkLineRenderer.apply {
            if (value == null || value.toString() == AbstractDetailPane.CPINFO_LINK_TEXT + "0") {
                foreground = standardForeground
                isUnderlined = false
            } else {
                foreground = HtmlDisplayTextArea.COLOR_LINK
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
            border = linkLineRenderer.border
            linkLineRenderer.border = null
        }
    }

    fun isLinkLabelHit(point: Point): Boolean {
        return linkLineRenderer.bounds.contains(point)
    }
}
