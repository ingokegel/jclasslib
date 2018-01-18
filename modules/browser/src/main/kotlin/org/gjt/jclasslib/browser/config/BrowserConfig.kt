/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config

import kotlinx.dom.build.addElement
import kotlinx.dom.childElements
import kotlinx.dom.firstChildElement
import org.gjt.jclasslib.browser.config.classpath.*
import org.w3c.dom.Element
import java.io.File
import java.util.*
import javax.swing.tree.DefaultTreeModel
import kotlin.properties.Delegates

class BrowserConfig : ClasspathComponent {

    val classpath: MutableList<ClasspathEntry> = ArrayList()
    var jreHome: String by Delegates.observable(System.getProperty("java.home")) { _, old, new ->
        if (old != new) {
            fireClasspathChanged(true)
        }
    }

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

    fun clear() {
        jreHome = System.getProperty("java.home")
        classpath.clear()
        mergedEntries.clear()
    }

    override fun findClass(className: String, modulePathSelection: Boolean): FindResult? {
        classpath.forEach { entry ->
            val findResult = entry.findClass(className, modulePathSelection)
            if (findResult != null) {
                return findResult
            }
        }
        return createJreEntry()?.findClass(className, modulePathSelection)
    }

    override fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        classpath.forEach { entry ->
            if (reset || !mergedEntries.contains(entry)) {
                entry.mergeClassesIntoTree(classPathModel, modulePathModel, reset)
                mergedEntries.add(entry)
            }
        }
        createJreEntry()?.mergeClassesIntoTree(classPathModel, modulePathModel, reset)
    }

    private fun createJreEntry(): ClasspathEntry? {
        return when {
            File(jreHome, "lib/modules").exists() -> ClasspathJrtEntry(jreHome)
            File(jreHome, "lib/rt.jar").exists() -> ClasspathArchiveEntry(File(jreHome, "lib/rt.jar").path)
            else -> return null
        }
    }

    private fun fireClasspathChanged(removal: Boolean) {
        val event = ClasspathChangeEvent(this, removal)
        changeListeners.forEach { listener -> listener.classpathChanged(event) }
    }

    fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME_CLASSPATH) {
            setAttribute(ATTRIBUTE_JRE_HOME, jreHome)
            classpath.forEach {
                it.saveWorkspace(this)
            }
        }
    }

    fun readWorkspace(element: Element) {
        clear()
        element.firstChildElement(NODE_NAME_CLASSPATH)?.let { classpathElement ->
            jreHome = classpathElement.getAttribute(ATTRIBUTE_JRE_HOME).let { if (it.isEmpty() || !File(it).exists()) jreHome else it }
            classpathElement.childElements().forEach { entryElement ->
                ClasspathEntry.create(entryElement)?.apply {
                    classpath.add(this)
                }
            }
        }
    }

    companion object {
        private const val ATTRIBUTE_JRE_HOME = "jreHome"
        private const val NODE_NAME_CLASSPATH = "classpath"
    }

}
