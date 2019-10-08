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
import java.io.FileInputStream
import javax.swing.tree.DefaultTreeModel

class ClasspathDirectoryEntry(fileName: String) : ClasspathEntry(fileName) {

    private val moduleName by lazy {
        val moduleInfoFile = File(file, MODULE_INFO_CLASS_FILE_NAME)
        if (moduleInfoFile.exists()) {
            getModuleName(FileInputStream(moduleInfoFile))
        } else {
            null
        } ?: UNNAMED_MODULE
    }

    override fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME) {
            setAttribute(ATTRIBUTE_PATH, file.path)
        }
    }

    override fun findClass(className: String, modulePathSelection: Boolean): FindResult? {
        if (!modulePathSelection || getModuleName(className) == moduleName) {
            val classFile = File(file, getClassPathClassName(className, modulePathSelection).replace('.', '/') + ".class")
            if (classFile.exists() && classFile.canRead()) {
                return FindResult(classFile.path, moduleName)
            }
        }
        return null
    }

    override fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        mergeClassesIntoClassPath(classPathModel, reset)
        mergeClassesIntoModulePath(modulePathModel, reset)
    }

    private fun mergeClassesIntoClassPath(classPathModel: DefaultTreeModel, reset: Boolean) {
        val rootNode = classPathModel.root as ClassTreeNode
        mergeDirectory(file, rootNode, classPathModel, reset)
    }

    private fun mergeClassesIntoModulePath(modulePathModel: DefaultTreeModel, reset: Boolean) {
        val rootNode = modulePathModel.root as ClassTreeNode
        val moduleNode = addOrFindNode(moduleName, rootNode, true, modulePathModel, reset)
        mergeDirectory(file, moduleNode, modulePathModel, reset)
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
            } else if (file.name.toLowerCase().endsWith(CLASSFILE_SUFFIX) &&
                    (!file.name.endsWith(MODULE_INFO_CLASS_FILE_NAME) || parentNode.parent != null)) {
                addOrFindNode(file.name.stripClassSuffix(), parentNode, false, model, reset)
            }
        }
    }

    companion object {
        const val NODE_NAME = "directory"
        private const val ATTRIBUTE_PATH = "path"

        fun create(element: Element) = ClasspathDirectoryEntry(element.getAttribute(ATTRIBUTE_PATH))
    }
}
