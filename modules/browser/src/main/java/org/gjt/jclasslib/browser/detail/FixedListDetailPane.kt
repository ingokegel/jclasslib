/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.AbstractDetailPane
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.util.ExtendedJLabel
import org.gjt.jclasslib.util.GUIHelper
import org.gjt.jclasslib.util.TextDisplay

import javax.swing.*
import javax.swing.tree.TreePath
import java.awt.*
import java.util.ArrayList

abstract class FixedListDetailPane(services: BrowserServices) : AbstractDetailPane(services) {

    private val detailPaneEntries = ArrayList<DetailPaneEntry>()
    private val scrollPane = JScrollPane(this).apply {
        GUIHelper.setDefaultScrollBarUnits(this)
        border = null
    }

    public override val wrapper: JComponent
        get() = scrollPane

    @JvmOverloads protected fun addDetailPaneEntry(key: ExtendedJLabel, value: TextDisplay?, comment: TextDisplay? = null) {
        detailPaneEntries.add(DetailPaneEntry(key, value, comment))
    }

    override fun setupComponent() {

        addLabels()
        layout = GridBagLayout()

        val gKey = GridBagConstraints().apply {
            anchor = GridBagConstraints.NORTHWEST
            insets = Insets(1, 10, 0, 10)
        }

        val gValue = GridBagConstraints().apply {
            gridx = 1
            anchor = GridBagConstraints.NORTHEAST
            insets = Insets(1, 0, 0, 5)
        }

        val gComment = GridBagConstraints().apply {
            gridx = 2
            anchor = GridBagConstraints.NORTHWEST
            insets = Insets(1, 0, 0, 5)
            fill = GridBagConstraints.HORIZONTAL
        }

        val gCommentOnly = (gComment.clone() as GridBagConstraints).apply {
            gridx = 1
            gridwidth = 2
        }

        val gRemainder = GridBagConstraints().apply {
            gridx = 2
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
        }

        detailPaneEntries.forEach { entry ->
            gKey.gridy++
            gComment.gridy++
            gValue.gridy++
            if (entry.key != null) {
                add(entry.key, gKey)
            }
            if (entry.value != null) {
                add(entry.value as JComponent, gValue)
            }
            if (entry.comment != null) {
                add(entry.comment as JComponent, if (entry.value == null) gCommentOnly else gComment)
                if (entry.comment is ExtendedJLabel) {
                    entry.comment.autoTooltip = true
                }
            }
        }

        gRemainder.gridy = gKey.gridy + 1
        gRemainder.gridy += addSpecial(gRemainder.gridy)

        add(JPanel(), gRemainder)
    }

    override fun show(treePath: TreePath) {
        scrollPane.viewport.viewPosition = Point(0, 0)
    }

    protected abstract fun addLabels()

    protected open fun addSpecial(gridy: Int): Int {
        return 0
    }

    private class DetailPaneEntry(val key: ExtendedJLabel?, val value: TextDisplay?, val comment: TextDisplay?)
}
