/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config.classpath

import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.browser.ClassFileCallback
import org.gjt.jclasslib.browser.handleClassFile
import org.gjt.jclasslib.io.findClassInJrt
import org.gjt.jclasslib.io.findClassWithModuleNameInJrt
import org.gjt.jclasslib.io.forEachClassInJrt
import org.gjt.jclasslib.io.forEachClassNameInJrt
import org.w3c.dom.Element
import java.nio.file.Path
import javax.swing.tree.DefaultTreeModel

class ClasspathJrtEntry(jreHome: String) : ClasspathFileEntry(jreHome) {
    override fun findClass(className: String, modulePathSelection: Boolean): FindResult? {
        return if (modulePathSelection) {
            findClassWithModuleNameInJrt(className, file)?.let { createFindResult(it, it.getName(1).toString()) }
        } else {
            findClassInJrt(className, file)?.let { createFindResult(it, UNNAMED_MODULE) }
        }
    }

    private fun createFindResult(path: Path, moduleName: String): FindResult = FindResult("$JRT_PREFIX$path", moduleName)

    override fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        forEachClassNameInJrt(file) { moduleName, path ->
            addEntry(path, moduleName, classPathModel, modulePathModel, reset)
        }
    }

    override fun scanClassFiles(classFileCallback: ClassFileCallback, includeJdk: Boolean, frame: BrowserFrame) {
        forEachClassInJrt(file) { moduleName, path ->
            classFileCallback.handleClassFile(createFindResult(path, moduleName), frame)
        }
    }

    override fun saveWorkspace(element: Element) {
        throw UnsupportedOperationException()
    }

    companion object {
        const val JRT_PREFIX = "jrt:"
    }
}
