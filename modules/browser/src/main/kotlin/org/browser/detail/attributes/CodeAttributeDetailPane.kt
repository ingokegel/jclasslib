/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.browser.detail.attributes.code.ByteCodeDetailPane
import org.gjt.jclasslib.browser.detail.attributes.code.ExceptionTableDetailPane
import org.gjt.jclasslib.browser.detail.attributes.code.MiscDetailPane
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.util.TabbedPaneFacade
import org.gjt.jclasslib.util.tabPaneFactory
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.tree.TreePath

class CodeAttributeDetailPane(services: BrowserServices) : DetailPane<CodeAttribute>(CodeAttribute::class.java, services) {

    val byteCodeDetailPane = ByteCodeDetailPane(services, this)
    val exceptionTableDetailPane = ExceptionTableDetailPane(services)
    val miscDetailPane = MiscDetailPane(services)

    private val detailsPanes: List<DetailPane<*>> = listOf(byteCodeDetailPane, exceptionTableDetailPane, miscDetailPane)
    private val displayComponentToDetailPane: Map<JComponent, DetailPane<*>> = detailsPanes.associateBy { it.displayComponent }

    private val tabbedPane: TabbedPaneFacade = tabPaneFactory().apply {
        detailsPanes.forEach {
            addTabAtEnd(it.name, it.displayComponent)
        }
    }

    override fun setupComponent() {
        layout = BorderLayout()
        add(tabbedPane.outerComponent, BorderLayout.CENTER)
    }

    fun selectByteCodeDetailPane() {
        tabbedPane.selectedTabIndex = detailsPanes.indexOf(byteCodeDetailPane)
    }

    fun selectExceptionTableDetailPane() {
        tabbedPane.selectedTabIndex = detailsPanes.indexOf(exceptionTableDetailPane)
    }

    override fun show(treePath: TreePath) {
        detailsPanes.forEach { it.show(treePath) }
    }

    override val clipboardText: String?
        get() = displayComponentToDetailPane[tabbedPane.selectedTab]?.clipboardText
}

