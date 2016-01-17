/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import java.io.IOException
import java.util.jar.JarFile
import javax.swing.tree.DefaultTreeModel

class ClasspathArchiveEntry : ClasspathEntry() {

    override fun findClass(className: String): FindResult? {
        val file = file ?: return null
        val internalClassName = className.replace('.', '/') + ".class"
        try {
            val jarFile = JarFile(file)
            val entry = jarFile.getJarEntry(internalClassName)
            if (entry != null) {
                return FindResult(file.path + "!" + internalClassName)
            }
        } catch (e: IOException) {
        }
        return null
    }

    override fun mergeClassesIntoTree(model: DefaultTreeModel, reset: Boolean) {
        val archive = file ?: return
        try {
            val jarFile = JarFile(archive)
            jarFile.entries().iterator().forEach {
                if (!it.isDirectory && it.name.toLowerCase().endsWith(ClasspathEntry.CLASSFILE_SUFFIX)) {
                    addEntry(stripClassSuffix(it.name), model, reset)
                }
            }
        } catch (ex: IOException) {
        }
    }

    private fun addEntry(path: String, model: DefaultTreeModel, reset: Boolean) {
        val pathComponents = path.replace('\\', '/').split(Regex("/"))
        var currentNode = model.root as ClassTreeNode
        pathComponents.forEachIndexed { i, pathComponent ->
            currentNode = addOrFindNode(pathComponent, currentNode, i < pathComponents.size - 1, model, reset)
        }
    }
}
