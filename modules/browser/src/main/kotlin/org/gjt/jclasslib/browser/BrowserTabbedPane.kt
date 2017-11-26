/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.browser.config.classpath.ClasspathEntry
import org.gjt.jclasslib.util.ClosableTabComponent
import org.gjt.jclasslib.util.DnDTabbedPane
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

class BrowserTabbedPane(val container: FrameContent) : DnDTabbedPane() {
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
        val title = if (browserTab.moduleName != ClasspathEntry.UNNAMED_MODULE) {
            browserTab.moduleName + "/"
        } else {
            ""
        } + browserTab.browserComponent.title
        addTab(title, browserTab)
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

