/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import org.gjt.jclasslib.browser.detail.attributes.document.AttributeDocument
import org.gjt.jclasslib.browser.detail.attributes.document.DocumentDetailPane
import util.LightOrDarkColor
import java.awt.Color
import java.awt.Component
import java.util.*
import javax.swing.Icon
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JTree
import javax.swing.border.Border
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.TreeModel

var treeFactory: (model: TreeModel) -> JTree = ::JTree
var treeCellRendererFactory: () -> DefaultTreeCellRenderer = ::DefaultTreeCellRenderer
var scrollPaneFactory: (component: Component) -> JScrollPane = ::JScrollPane
var borderlessScrollPaneFactory: (component: Component) -> JScrollPane = ::BorderLessScrollPane

class BorderLessScrollPane(component: Component): JScrollPane(component) {
    init {
        border = null
    }
    override fun setBorder(border: Border?) {
    }
}

enum class TreeIcon {CLOSED, OPEN, LEAF}

private val trees = WeakHashMap<JTree, Unit>()
fun JTree.applyTreeRowHeight() {
    trees[this] = Unit
    applyTreeRowHeightInternal()
}

private fun JTree.applyTreeRowHeightInternal() {
    if (treeRowHeight > 0) {
        rowHeight = treeRowHeight
    }
}

var treeRowHeight : Int = 0
    set(value) {
        field = value
        trees.keys.filterNotNull().forEach { it.applyTreeRowHeightInternal() }
    }

private val tables = WeakHashMap<JTable, Float>()
fun JTable.applyTableRowHeight(rowHeightFactor: Float) {
    tables[this] = rowHeightFactor
    applyTableRowHeightInternal(rowHeightFactor)
}

private fun JTable.applyTableRowHeightInternal(rowHeightFactor: Float) {
    val baseRowHeight = tableRowHeight.takeIf { it > 0 } ?: rowHeight
    rowHeight = (baseRowHeight * rowHeightFactor).toInt()
}

var tableRowHeight : Int = 0
    set(value) {
        field = value
        tables.entries.forEach { (tree, rowHeightFactor) ->
            tree?.applyTableRowHeightInternal(rowHeightFactor)
        }
    }

var documentFontFamily : String? = null
    set(value) {
        field = value
        AttributeDocument.updateFontFamilies()
        DocumentDetailPane.clearDocuments()
    }

var documentFontSize: Int = 12
    set(value) {
        field = value
        AttributeDocument.updateFontSizes()
        DocumentDetailPane.clearDocuments()
    }

var treeIcons: Map<TreeIcon, Icon> = emptyMap()

enum class ColorKey {LINK, ACTIVE_LINK, VALUE}

var colors: Map<ColorKey, Color> = emptyMap()
var darkMode = false

fun getLinkColor() = colors[ColorKey.LINK] ?: LightOrDarkColor(Color(0, 77, 145), Color(88, 157, 246))
fun getActiveLinkColor() = colors[ColorKey.ACTIVE_LINK] ?: LightOrDarkColor(Color(225, 9, 21), Color(186, 51, 36))
fun getValueColor() = colors[ColorKey.VALUE] ?:  LightOrDarkColor(Color(0, 110, 0), Color(60, 140, 60))