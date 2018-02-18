/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import javax.swing.tree.DefaultTreeModel

interface ClasspathComponent {
    fun findClass(className: String, modulePathSelection: Boolean): FindResult?
    fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean)
    fun contains(component: ClasspathComponent): Boolean
}

data class FindResult(val fileName: String, val moduleName: String = ClasspathEntry.UNNAMED_MODULE)
