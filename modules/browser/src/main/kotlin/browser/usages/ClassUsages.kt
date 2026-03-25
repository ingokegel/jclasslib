/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.usages

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.GlobalBrowserServices
import org.gjt.jclasslib.browser.NodeType
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.browser.config.CategoryHolder
import org.gjt.jclasslib.browser.config.IndexHolder
import org.gjt.jclasslib.io.ClassFileReadMode
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.util.*
import java.awt.Component
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.*

fun showNoUsagesFoundMessage(parent: Component) {
    alertFacade.showMessage(parent, getString("no.usages.found"), AlertType.INFORMATION)
}

fun findClassUsages(services: GlobalBrowserServices, includeJdk: Boolean, parentWindow: Window?, predicate: (Constant) -> Boolean): List<ClassUsage> {
    val classUsages = mutableListOf<ClassUsage>()
    ProgressDialog(parentWindow, getString("searching.usages")) {
        services.scanClassFiles(includeJdk, readMode = ClassFileReadMode.SKIP_ATTRIBUTES) { classFile, _ ->
            classFile.constantPool.filter(predicate).forEach { constant ->
                classUsages.add(ClassUsage(classFile.thisClassName, classFile.getConstantPoolIndex(constant)))
            }
        }
    }.apply {
        isVisible = true
    }
    return classUsages
}

fun showClassUsages(classUsages: List<ClassUsage>, services: GlobalBrowserServices, parentWindow: Window?) {
    val selected = showClassListDialog(classUsages, parentWindow, getString("found.classes.with.usages.title"), getString("multiple.classes.with.usages.info"))
    for (classUsage in selected) {
        services.openClassFile(classUsage.className, BrowserPath().apply {
            addPathComponent(CategoryHolder(NodeType.CONSTANT_POOL))
            addPathComponent(IndexHolder(classUsage.referenceIndex - 1))
        })
    }
}

class ClassUsage(val className: String, val referenceIndex: Int) {
    override fun toString() = className.replace('/', '.') + " [${String.format("%03d", referenceIndex)}]"
}

fun <T> showClassListDialog(items: List<T>, parentWindow: Window?, title: String, infoMessage: String): List<T> {
    val dialog = ClassListDialog(items, parentWindow, title, infoMessage)
    dialog.isVisible = true
    return if (!dialog.isCanceled) dialog.selectedItems else emptyList()
}

private class ClassListDialog<T>(private val items: List<T>, parentWindow: Window?, title: String, private val infoMessage: String) :
    StandardDialog(parentWindow, title) {
    private val list = JList(Vector(items)).apply {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && selectedValue != null) {
                    doOk()
                }
            }
        })
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
    }

    init {
        setupComponent()
    }

    @Suppress("UNCHECKED_CAST")
    val selectedItems: List<T> get() = list.selectedValuesList as List<T>

    override fun addContent(component: JComponent) {
        with(component) {
            layout = MigLayout("wrap", "[grow]")
            add(JLabel(getString("results.count", items.size)))
            add(JScrollPane(list), "pushy, grow")
            add(HtmlDisplayTextArea(infoMessage).apply {
                isEnabled = false
            }, "growx")
        }
        setSize(600, 400)
        addWindowListener(object: WindowAdapter() {
            override fun windowOpened(e: WindowEvent?) {
                revalidate()
            }
        })
    }
}