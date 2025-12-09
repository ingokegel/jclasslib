/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config

import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.browser.ClassFileCallback
import org.gjt.jclasslib.browser.config.classpath.*
import java.io.File
import javax.swing.tree.DefaultTreeModel

abstract class ClassPathContainer : ClasspathComponent {

    abstract val classpath: List<ClasspathEntry>
    abstract val jreHome: String

    private val mergedEntries = HashSet<ClasspathEntry>()

    override fun findClass(className: String, modulePathSelection: Boolean): FindResult? {
        for (entry in classpath) {
            val findResult = entry.findClass(className, modulePathSelection)
            if (findResult != null) {
                return findResult
            }
        }
        return createJreEntry()?.findClass(className, modulePathSelection)
    }

    override fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        for (entry in classpath) {
            if (reset || !mergedEntries.contains(entry)) {
                entry.mergeClassesIntoTree(classPathModel, modulePathModel, reset)
                mergedEntries.add(entry)
            }
        }
        createJreEntry()?.mergeClassesIntoTree(classPathModel, modulePathModel, reset)
    }

    override fun scanClassFiles(classFileCallback: ClassFileCallback, includeJdk: Boolean, frame: BrowserFrame) {
        for (entry in classpath) {
            entry.scanClassFiles(classFileCallback, includeJdk, frame)
        }
        if (includeJdk) {
            createJreEntry()?.scanClassFiles(classFileCallback, true, frame)
        }
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
            else -> null
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