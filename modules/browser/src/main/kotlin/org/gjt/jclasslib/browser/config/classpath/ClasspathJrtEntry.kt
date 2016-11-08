/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config.classpath

import org.gjt.jclasslib.io.findClassInJrt
import org.gjt.jclasslib.io.forEachClassNameInJrt
import org.w3c.dom.Element
import javax.swing.tree.DefaultTreeModel

class ClasspathJrtEntry(jreHome: String) : ClasspathEntry(jreHome) {
    override fun findClass(className: String) = findClassInJrt(className, file)?.let { FindResult("$JRT_PREFIX$it") }

    override fun mergeClassesIntoTree(model: DefaultTreeModel, reset: Boolean) {
        forEachClassNameInJrt(file) { path ->
            addEntry(path, model, reset)
        }
    }

    override fun saveWorkspace(element: Element) {
        throw UnsupportedOperationException()
    }

    companion object {
        val JRT_PREFIX = "jrt:"
    }
}
