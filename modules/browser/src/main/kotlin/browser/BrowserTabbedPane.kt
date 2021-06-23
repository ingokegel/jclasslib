/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import com.install4j.runtime.alert.AlertType
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.util.ClosableTabComponent
import org.gjt.jclasslib.util.DnDTabbedPane
import org.gjt.jclasslib.util.GUIHelper
import java.awt.Component
import java.awt.EventQueue
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTargetDropEvent
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.Icon

class BrowserTabbedPane(val container: FrameContent) : DnDTabbedPane(), ClosableTabComponent.RemovalChecker {
    init {
        addFocusListener(object : FocusAdapter() {
            override fun focusGained(event: FocusEvent) {
                focus()
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(event: MouseEvent) {
                focus()
            }
        })
    }

    val selectedTab: BrowserTab?
        get() = selectedComponent as BrowserTab?

    fun transferTabsFrom(other: BrowserTabbedPane) {
        other.tabs().toList().forEach { addTab(it) }
    }

    fun addTab(fileName: String, moduleName: String, browserPath: BrowserPath? = null) =
            BrowserTab(fileName, moduleName, container.frame).apply {
                addTab(this)
                setBrowserPath(browserPath)
            }

    fun addTab(browserTab: BrowserTab) {
        addTab(browserTab.getTabTitle(), browserTab)
        selectedComponent = browserTab
    }

    override fun insertTab(title: String?, icon: Icon?, component: Component, tip: String?, index: Int) {
        super.insertTab(title, icon, component, tip, index)
        setTabComponentAt(index, ClosableTabComponent(this))
        fireStateChanged()
    }

    fun tabs() = Iterable {
        object : Iterator<BrowserTab> {
            var index = 0
            override fun next() = getComponentAt(index++) as BrowserTab
            override fun hasNext() = index < tabCount
        }
    }

    override fun setSelectedComponent(component: Component) {
        super.setSelectedComponent(component)
        focus()
    }

    override fun removeAll() {
        super.removeAll()
        fireStateChanged()
    }

    fun focus() {
        container.focus(this@BrowserTabbedPane)
        selectedTab?.browserComponent?.history?.updateActions()
    }

    fun updateSelectedTitle() {
        updateTitleAt(selectedIndex)
    }

    fun updateTitleAt(index: Int) {
        setTitleAt(index, getBrowserTabAt(index).getTabTitle())
        getTabComponentAt(index).revalidate()
    }

    fun updateTitleOf(browserTab: BrowserTab) {
        val index = indexOfComponent(browserTab)
        if (index > -1) {
            updateTitleAt(index)
        }
    }

    override fun canRemove(index: Int): Boolean = canRemove(getBrowserTabAt(index))

    fun canRemove(browserTab: BrowserTab): Boolean =
        !browserTab.isModified || GUIHelper.showOptionDialog(
                this,
                getString("message.class.file.modified.title"),
                getString("message.class.file.modified"),
                GUIHelper.DISCARD_CANCEL_OPTIONS,
                AlertType.QUESTION
        ) == 0

    private fun getBrowserTabAt(index: Int) = getComponentAt(index) as BrowserTab

    override fun removed(index: Int) {
        container.updateSaveAction()
    }

    fun canClose(): Boolean = !hasModified() || GUIHelper.showOptionDialog(
            this,
            getString("message.class.files.modified.title"),
            getString("message.class.files.modified"),
            GUIHelper.DISCARD_CANCEL_OPTIONS,
            AlertType.QUESTION
    ) == 0

    fun hasModified() = tabs().any { it.isModified }

    override fun isDataFlavorSupported(transferable: Transferable): Boolean =
            transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)

    override fun handleDrop(event: DropTargetDropEvent) {
        event.acceptDrop(DnDConstants.ACTION_COPY)
        val files = (event.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>).map { it as File }
        EventQueue.invokeLater {
            focus()
            if (files.size == 1 && files.all { it.extension == "jar" }) {
                container.frame.openClassFromJar(files[0])
                event.dropComplete(true)
            } else if (files.all { it.extension == "class" }) {
                files.forEach { file ->
                    container.frame.openClassFromFile(file)
                }
                event.dropComplete(true)
            } else {
                event.dropComplete(false)
            }
        }
    }
}

