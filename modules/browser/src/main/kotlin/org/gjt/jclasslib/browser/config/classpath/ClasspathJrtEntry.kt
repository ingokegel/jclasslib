/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config.classpath

import org.gjt.jclasslib.browser.enumerateJrtClasses
import org.gjt.jclasslib.browser.findClassInJrt
import org.w3c.dom.Element
import java.nio.file.Files
import javax.swing.tree.DefaultTreeModel

class ClasspathJrtEntry(jreHome : String) : ClasspathEntry(jreHome) {
    override fun findClass(className: String) = findClassInJrt(className, file)

    override fun mergeClassesIntoTree(model: DefaultTreeModel, reset: Boolean) {
        enumerateJrtClasses(file) { path ->
            if (!Files.isDirectory(path) && path.toString().toLowerCase().endsWith(CLASSFILE_SUFFIX)) {
                addEntry(path.toString(), model, reset)
            }
        }
    }

    override fun saveWorkspace(element: Element) {
        throw UnsupportedOperationException()
    }
}
