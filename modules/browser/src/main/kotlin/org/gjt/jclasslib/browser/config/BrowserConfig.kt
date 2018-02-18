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
import org.gjt.jclasslib.browser.config.classpath.ClasspathArchiveEntry
import org.gjt.jclasslib.browser.config.classpath.ClasspathDirectoryEntry
import org.gjt.jclasslib.browser.config.classpath.ClasspathEntry
import org.w3c.dom.Element
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class BrowserConfig : ClassPathContainer() {

    override val classpath: MutableList<ClasspathEntry> = ArrayList()
    override var jreHome: String = System.getProperty("java.home")

    private val mergedEntries = HashSet<ClasspathEntry>()

    fun toImmutableContainer() : ImmutableClassPathContainer = ImmutableClassPathContainer(ArrayList(classpath), jreHome)

    fun addClasspathDirectory(directoryName: String) {
        ClasspathDirectoryEntry(directoryName).apply {
            addToClassPath(classpath)
        }
    }

    fun addClasspathArchive(archiveName: String) {
        ClasspathArchiveEntry(archiveName).apply {
            addToClassPath(classpath)
        }
    }

    fun addClasspathEntry(entry: ClasspathEntry) {
        entry.addToClassPath(classpath)
    }

    fun removeClasspathEntry(entry: ClasspathEntry) {
        classpath.remove(entry)
    }

    fun clear() {
        jreHome = System.getProperty("java.home")
        classpath.clear()
        mergedEntries.clear()
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
