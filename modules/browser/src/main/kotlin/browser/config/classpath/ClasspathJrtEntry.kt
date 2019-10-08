/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config.classpath

import org.gjt.jclasslib.io.findClassInJrt
import org.gjt.jclasslib.io.findClassWithModuleNameInJrt
import org.gjt.jclasslib.io.forEachClassNameInJrt
import org.w3c.dom.Element
import javax.swing.tree.DefaultTreeModel

class ClasspathJrtEntry(jreHome: String) : ClasspathEntry(jreHome) {
    override fun findClass(className: String, modulePathSelection: Boolean): FindResult? {
        return if (modulePathSelection) {
            findClassWithModuleNameInJrt(className, file)?.let { FindResult("$JRT_PREFIX$it", it.getName(1).toString()) }
        } else {
            findClassInJrt(className, file)?.let { FindResult("$JRT_PREFIX$it") }
        }
    }

    override fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        forEachClassNameInJrt(file) { moduleName, path ->
            addEntry(path, moduleName, classPathModel, modulePathModel, reset)
        }
    }

    override fun saveWorkspace(element: Element) {
        throw UnsupportedOperationException()
    }

    companion object {
        const val JRT_PREFIX = "jrt:"
    }
}
