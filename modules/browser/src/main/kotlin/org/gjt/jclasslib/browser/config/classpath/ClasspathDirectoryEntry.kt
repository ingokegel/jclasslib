/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import kotlinx.dom.build.addElement
import org.w3c.dom.Element
import java.io.File
import javax.swing.tree.DefaultTreeModel

class ClasspathDirectoryEntry(fileName : String) : ClasspathEntry(fileName) {

    override fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME) {
            setAttribute(ATTRIBUTE_PATH, file.path)
        }
    }

    override fun findClass(className: String): FindResult? {
        val classFile = File(file, className.replace('.', '/') + ".class")
        if (classFile.exists() && classFile.canRead()) {
            return FindResult(classFile.path)
        }
        return null
    }

    override fun mergeClassesIntoTree(model: DefaultTreeModel, reset: Boolean) {
        val rootNode = model.root as ClassTreeNode
        mergeDirectory(file, rootNode, model, reset)
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

    companion object {
        val NODE_NAME = "directory"
        private val ATTRIBUTE_PATH = "path"

        fun create(element: Element) : ClasspathDirectoryEntry? {
            val path = element.getAttribute(ATTRIBUTE_PATH)
            return path?.let { ClasspathDirectoryEntry(path) } ?: null
        }
    }
}
