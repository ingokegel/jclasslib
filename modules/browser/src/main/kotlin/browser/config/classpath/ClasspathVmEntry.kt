/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config.classpath

import org.gjt.jclasslib.browser.VmConnection
import org.w3c.dom.Element
import javax.swing.tree.DefaultTreeModel

class ClasspathVmEntry(private val vmConnection: VmConnection) : ClasspathEntry() {
    override fun findClass(className: String, modulePathSelection: Boolean): FindResult? {
        val classNameWithoutModule = if (modulePathSelection) className.substringAfter("/") else className
        val displayClassName = classNameWithoutModule.replace('/', '.')
        val classDescriptor = vmConnection.communicator.classes.find { it.className == displayClassName }
        return if (classDescriptor != null) {
            FindResult(classNameWithoutModule, classDescriptor.moduleName ?: UNNAMED_MODULE)
        } else {
            null
        }
    }

    override fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        vmConnection.communicator.classes
                // classes with a / in their name are hidden classes
                .filterNot { it.className.contains('/') }
                .forEach {
                    addEntry(it.className.replace('.', '/'), it.moduleName, classPathModel, modulePathModel, reset)
                }
    }

    override fun saveWorkspace(element: Element) {
        throw UnsupportedOperationException()
    }
}