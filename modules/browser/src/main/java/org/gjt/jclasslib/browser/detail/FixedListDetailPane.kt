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

    private val scrollPane = JScrollPane(this).apply {
        GUIHelper.setDefaultScrollBarUnits(this)
        border = null
    }
    private var currentY = 0

    public override val wrapper: JComponent
        get() = scrollPane

    @JvmOverloads protected fun addDetailPaneEntry(key: ExtendedJLabel, value: TextDisplay?, comment: TextDisplay? = null) {
        add(key, gc() {
            insets = Insets(1, 10, 0, 10)
        })
        if (value != null) {
            add(value as JComponent, gc() {
                gridx = 1
                insets = Insets(1, 0, 0, 5)
                if (comment == null) {
                    gridwidth = 2
                }
            })
        }
        if (comment != null) {
            add(comment as JComponent, gc() {
                if (value == null) {
                    gridx = 1
                    gridwidth = 2
                } else {
                    gridx = 2
                }
                insets = Insets(1, 0, 0, 5)
                fill = GridBagConstraints.HORIZONTAL
            })
            if (comment is ExtendedJLabel) {
                comment.autoTooltip = true
            }
        }
        nextLine()
    }

    protected fun nextLine() {
        currentY++
    }

    override fun setupComponent() {
        layout = GridBagLayout()
        addLabels()
        add(JPanel(), gc() {
            gridx = 2
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
        })
    }

    protected fun gc(config: GridBagConstraints.() -> Unit) = GridBagConstraints().apply {
        anchor = GridBagConstraints.NORTHWEST
        gridy = currentY
        config()
    }

    override fun show(treePath: TreePath) {
        scrollPane.viewport.viewPosition = Point(0, 0)
    }

    protected abstract fun addLabels()

    protected open fun addSpecial(gridy: Int): Int {
        return 0
    }

}
