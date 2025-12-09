/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config.classpath

import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.browser.ClassFileCallback
import org.gjt.jclasslib.browser.VmConnection
import org.gjt.jclasslib.browser.handleClassFile
import org.jclasslib.agent.ClassDescriptor
import org.w3c.dom.Element
import javax.swing.tree.DefaultTreeModel

class ClasspathVmEntry(private val vmConnection: VmConnection) : ClasspathEntry() {
    override fun findClass(className: String, modulePathSelection: Boolean): FindResult? {
        val classNameWithoutModule = if (modulePathSelection) className.substringAfter("/") else className
        val displayClassName = classNameWithoutModule.replace('/', '.')
        val classDescriptor = vmConnection.communicator.classes.find { it.className == displayClassName }
        return if (classDescriptor != null) {
            createFindResult(classDescriptor)
        } else {
            null
        }
    }

    override fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        forEachClass {
            addEntry(it.className.replace('.', '/'), it.moduleName, classPathModel, modulePathModel, reset)
        }
    }

    override fun scanClassFiles(classFileCallback: ClassFileCallback, includeJdk: Boolean, frame: BrowserFrame) {
        forEachClass {
            classFileCallback.handleClassFile(createFindResult(it), frame)
        }
    }

    private fun forEachClass(handler: (ClassDescriptor) -> Unit) {
        vmConnection.communicator.classes
            // classes with a / in their name are hidden classes
            .filterNot { it.className.contains('/') }
            .forEach { handler(it) }
    }

    private fun createFindResult(classDescriptor: ClassDescriptor): FindResult =
        FindResult(classDescriptor.className.replace('.', '/'), classDescriptor.moduleName ?: UNNAMED_MODULE)

    override fun saveWorkspace(element: Element) {
        throw UnsupportedOperationException()
    }
}