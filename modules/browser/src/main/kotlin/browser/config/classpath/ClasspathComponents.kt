/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.browser.ClassFileCallback
import org.gjt.jclasslib.io.ClassFileReadMode
import javax.swing.tree.DefaultTreeModel

interface ClasspathComponent {
    fun findClass(className: String, modulePathSelection: Boolean): FindResult?
    fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean)
    fun contains(component: ClasspathComponent): Boolean
    fun scanClassFiles(classFileCallback: ClassFileCallback, includeJdk: Boolean, frame: BrowserFrame, readMode: ClassFileReadMode = ClassFileReadMode.FULL)
}

data class FindResult(val fileName: String, val moduleName: String = ClasspathEntry.UNNAMED_MODULE)
