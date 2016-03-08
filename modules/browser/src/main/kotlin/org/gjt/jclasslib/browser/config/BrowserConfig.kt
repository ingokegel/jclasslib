/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config

import kotlinx.dom.build.addElement
import kotlinx.dom.childElements
import org.gjt.jclasslib.browser.config.classpath.*
import org.w3c.dom.Element
import java.io.File
import java.util.*
import javax.swing.tree.DefaultTreeModel

class BrowserConfig : ClasspathComponent {

    val classpath: MutableList<ClasspathEntry> = ArrayList()

    private val mergedEntries = HashSet<ClasspathEntry>()
    private val changeListeners = HashSet<ClasspathChangeListener>()

    override fun addClasspathChangeListener(listener: ClasspathChangeListener) {
        changeListeners.add(listener)
    }

    override fun removeClasspathChangeListener(listener: ClasspathChangeListener) {
        changeListeners.remove(listener)
    }

    fun addClasspathDirectory(directoryName: String) {
        ClasspathDirectoryEntry(directoryName).apply {
            if (addToClassPath(classpath)) {
                fireClasspathChanged(false)
            }
        }
    }

    fun addClasspathArchive(archiveName: String) {
        ClasspathArchiveEntry(archiveName).apply {
            if (addToClassPath(classpath)) {
                fireClasspathChanged(false)
            }
        }
    }

    fun addClasspathEntry(entry: ClasspathEntry) {
        if (entry.addToClassPath(classpath)) {
            fireClasspathChanged(false)
        }
    }

    fun removeClasspathEntry(entry: ClasspathEntry) {
        if (classpath.remove(entry)) {
            fireClasspathChanged(true)
        }
    }

    fun addRuntimeLib() {
        val fileName = String::class.java.getResource("String.class").toExternalForm()

        val matchResult = Regex("jar:file:/(.*)!.*").matchEntire(fileName)
        if (matchResult != null) {
            val path = matchResult.groups[1]?.value!!
            addClasspathArchive(File(if (path.contains(':')) path else "/" + path).path)
            fireClasspathChanged(false)
        }
    }

    override fun findClass(className: String): FindResult? {
        classpath.forEach { entry ->
            val findResult = entry.findClass(className)
            if (findResult != null) {
                return findResult
            }
        }
        return null
    }

    override fun mergeClassesIntoTree(model: DefaultTreeModel, reset: Boolean) {
        classpath.forEach { entry ->
            if (reset || !mergedEntries.contains(entry)) {
                entry.mergeClassesIntoTree(model, reset)
                mergedEntries.add(entry)
            }
        }
    }

    private fun fireClasspathChanged(removal: Boolean) {
        val event = ClasspathChangeEvent(this, removal)
        changeListeners.forEach { listener -> listener.classpathChanged(event) }
    }

    fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME_CLASSPATH) {
            classpath.forEach {
                it.saveWorkspace(this)
            }
        }
    }

    fun readWorkspace(element: Element) {
        classpath.clear()
        mergedEntries.clear()
        element.childElements(NODE_NAME_CLASSPATH).firstOrNull()?.childElements()?.forEach { entryElement ->
            ClasspathEntry.create(entryElement)?.apply {
                classpath.add(this)
            }
        }
    }

    companion object {
        private val NODE_NAME_CLASSPATH = "classpath"
    }

}
