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
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.HtmlDisplayTextArea
import org.gjt.jclasslib.util.ProgressDialog
import org.gjt.jclasslib.util.StandardDialog
import org.gjt.jclasslib.util.alertFacade
import java.awt.Component
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel

fun showNoUsagesFoundMessage(parent: Component) {
    alertFacade.showMessage(parent, getString("no.usages.found"), AlertType.INFORMATION)
}

fun findClassUsages(services: GlobalBrowserServices, includeJdk: Boolean, parentWindow: Window?, predicate: (Constant) -> Boolean): List<ClassUsage> {
    val classUsages = mutableListOf<ClassUsage>()
    ProgressDialog(parentWindow, getString("searching.usages")) {
        services.scanClassFiles(includeJdk) { classFile, _ ->
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
    ClassUsagesDialog(classUsages, parentWindow).apply {
        isVisible = true
        if (!isCanceled) {
            for (classUsage in selectedClassUsages) {
                services.openClassFile(classUsage.className, BrowserPath().apply {
                    addPathComponent(CategoryHolder(NodeType.CONSTANT_POOL))
                    addPathComponent(IndexHolder(classUsage.referenceIndex - 1))
                })
            }
        }
    }
}

class ClassUsage(val className: String, val referenceIndex: Int) {
    override fun toString() = className.replace('/', '.') + " [${String.format("%03d", referenceIndex)}]"
}

private class ClassUsagesDialog(classNames: List<ClassUsage>, parentWindow: Window?) :
    StandardDialog(parentWindow, getString("found.classes.with.usages.title")) {
    private val list = JList(classNames.toTypedArray()).apply {
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

    val selectedClassUsages: List<ClassUsage> get() = list.selectedValuesList

    override fun addContent(component: JComponent) {
        with(component) {
            layout = MigLayout("wrap", "[grow]")
            add(JScrollPane(list), "pushy, grow")
            add(HtmlDisplayTextArea(getString("multiple.classes.with.usages.info")).apply {
                isEnabled = false
            }, "growx")
        }
        setSize(600, 400)
    }
}