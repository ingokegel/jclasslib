/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.attach

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.AttachableVm
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.util.SelectionDialog
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.JScrollPane

class AttachDialog(vms: List<AttachableVm>, parentWindow: Window?): SelectionDialog<AttachableVm>(parentWindow, getString("window.select.running.jvm")) {
    private val list = JList(vms.toTypedArray()).apply {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && selectedValue != null) {
                    doOk()
                }
            }
        })
    }

    init {
        setupComponent()
    }

    override val selectedItem: AttachableVm?
        get() = list.selectedValue

    override fun addContent(component: JComponent) {
        with (component) {
            layout = MigLayout("wrap", "[grow]")
            add(JScrollPane(list), "pushy, grow")
        }
        setSize(600, 400)
    }
}