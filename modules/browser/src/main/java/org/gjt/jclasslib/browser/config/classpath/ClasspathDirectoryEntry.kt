/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import java.io.File
import javax.swing.tree.DefaultTreeModel

class ClasspathDirectoryEntry : ClasspathEntry() {

    override fun findClass(className: String): FindResult? {
        val file = file ?: return null
        val classFile = File(file, className.replace('.', '/') + ".class")
        if (classFile.exists() && classFile.canRead()) {
            return FindResult(this, classFile.path)
        }
        return null
    }

    override fun mergeClassesIntoTree(model: DefaultTreeModel, reset: Boolean) {
        val directory = file ?: return
        val rootNode = model.root as ClassTreeNode
        mergeDirectory(directory, rootNode, model, reset)
    }

    private fun mergeDirectory(directory: File, parentNode: ClassTreeNode, model: DefaultTreeModel, reset: Boolean) {
        val files = directory.listFiles() ?: return
        files.forEach { file ->
            if (file.isDirectory) {
                val directoryNode = addOrFindNode(file.name, parentNode, true, model, reset)
                mergeDirectory(file, directoryNode, model, reset)
                if (directoryNode.childCount == 0) {
                    val deletionIndex = parentNode.getIndex(directoryNode)
                    parentNode.remove(directoryNode)
                    if (!reset) {
                        model.nodesWereRemoved(parentNode, intArrayOf(deletionIndex), arrayOf<Any>(directoryNode))
                    }
                }
            } else if (file.name.toLowerCase().endsWith(ClasspathEntry.CLASSFILE_SUFFIX)) {
                addOrFindNode(stripClassSuffix(file.name), parentNode, false, model, reset)
            }
        }

    }

}
