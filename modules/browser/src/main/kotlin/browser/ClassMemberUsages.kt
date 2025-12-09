/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.browser.config.CategoryHolder
import org.gjt.jclasslib.browser.config.IndexHolder
import org.gjt.jclasslib.structures.ClassMember
import org.gjt.jclasslib.structures.FieldInfo
import org.gjt.jclasslib.structures.MethodInfo
import org.gjt.jclasslib.structures.constants.ConstantFieldrefInfo
import org.gjt.jclasslib.structures.constants.ConstantInterfaceMethodrefInfo
import org.gjt.jclasslib.structures.constants.ConstantMethodrefInfo
import org.gjt.jclasslib.util.HtmlDisplayTextArea
import org.gjt.jclasslib.util.ProgressDialog
import org.gjt.jclasslib.util.StandardDialog
import org.gjt.jclasslib.util.getParentWindow
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel

private val JDK_PREFIXES = listOf("java/", "javax/", "jdk/", "sun/", "com/sun/")

fun findClassMemberUsages(browserComponent: BrowserComponent, classMember: ClassMember) {
    val classUsages = mutableListOf<ClassUsage>()
    ProgressDialog(browserComponent.getParentWindow(), getString("searching.usages")) {
        findClassUsages(browserComponent, classMember, classUsages)
    }.apply {
        isVisible = true
    }
    if (classUsages.isNotEmpty()) {
        val classFile = browserComponent.services.classFile
        if (classUsages.size == 1 && classUsages.first().className == classFile.thisClassName) {
            findConstantUsages(browserComponent, classFile.constantPool[classUsages.first().referenceIndex])
        } else {
            ClassUsagesDialog(classUsages, browserComponent.getParentWindow()).apply {
                isVisible = true
                if (!isCanceled) {
                    for (classUsage in selectedClassUsages) {
                        browserComponent.services.openClassFile(classUsage.className, BrowserPath().apply {
                            addPathComponent(CategoryHolder(NodeType.CONSTANT_POOL))
                            addPathComponent(IndexHolder(classUsage.referenceIndex - 1))
                        })
                    }
                }
            }
        }
    } else {
        showNoUsagesFoundMessage(browserComponent)
    }
}

private fun findClassUsages(
    browserComponent: BrowserComponent,
    classMember: ClassMember,
    classUsages: MutableList<ClassUsage>
) {
    val className = browserComponent.services.classFile.thisClassName
    val classMemberName = classMember.name
    val classMemberDescriptor = classMember.descriptor

    val includeJdk = JDK_PREFIXES.any { className.startsWith(it) }
    browserComponent.services.scanClassFiles(includeJdk) { classFile, _ ->
        val reference = classFile.constantPool.find { constant ->
            if ((classMember is FieldInfo && constant is ConstantFieldrefInfo) ||
                (classMember is MethodInfo && (constant is ConstantMethodrefInfo || constant is ConstantInterfaceMethodrefInfo))
            ) {
                val nameAndTypeConstant = constant.nameAndTypeConstant
                constant.classConstant.name == className &&
                        nameAndTypeConstant.name == classMemberName &&
                        nameAndTypeConstant.descriptor == classMemberDescriptor
            } else {
                false
            }
        }
        if (reference != null) {
            classUsages.add(ClassUsage(classFile.thisClassName, classFile.getConstantPoolIndex(reference)))
        }
    }
}

private class ClassUsage(val className: String, val referenceIndex: Int) {
    override fun toString() = className.replace('/', '.')
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