/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config

import org.gjt.jclasslib.browser.config.classpath.*
import java.io.File
import java.util.*
import javax.swing.tree.DefaultTreeModel

abstract class ClassPathContainer : ClasspathComponent {

    abstract val classpath: List<ClasspathEntry>
    abstract val jreHome: String

    private val mergedEntries = HashSet<ClasspathEntry>()

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

    override fun contains(component: ClasspathComponent): Boolean = if (component is ClassPathContainer) {
        jreHome == component.jreHome && classpath.containsAll(component.classpath)
    } else {
        false
    }

    private fun createJreEntry(): ClasspathEntry? {
        return when {
            File(jreHome, "lib/modules").exists() -> ClasspathJrtEntry(jreHome)
            File(jreHome, "lib/rt.jar").exists() -> ClasspathArchiveEntry(File(jreHome, "lib/rt.jar").path)
            else -> return null
        }
    }
}

class ImmutableClassPathContainer(override val classpath: List<ClasspathEntry>, override val jreHome: String) : ClassPathContainer() {
    override fun equals(other: Any?): Boolean = if (other is ImmutableClassPathContainer) {
        other.classpath == classpath && other.jreHome == jreHome
    } else {
        false
    }

    override fun hashCode(): Int {
        return classpath.hashCode() * 31 + jreHome.hashCode()
    }
}