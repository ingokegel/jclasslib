/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.structures.attributes.ModuleAttribute
import org.w3c.dom.Element
import java.io.File
import java.io.InputStream
import javax.swing.tree.DefaultTreeModel

abstract class ClasspathEntry(fileName : String) : ClasspathComponent {

    val file : File = File(fileName).canonicalFile

    abstract fun saveWorkspace(element: Element)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || other::class.java != this::class.java) {
            return false
        }
        other as ClasspathEntry

        return file == other.file
    }

    override fun hashCode(): Int = file.hashCode()

    override fun contains(component: ClasspathComponent): Boolean = false

    protected fun addOrFindNode(newNodeName: String,
                                parentNode: ClassTreeNode,
                                packageNode: Boolean,
                                model: DefaultTreeModel,
                                reset: Boolean): ClassTreeNode {
        val childCount = parentNode.childCount

        val newNode = ClassTreeNode(newNodeName, packageNode)
        for (i in 0 until childCount) {
            val childNode = parentNode.getChildAt(i) as ClassTreeNode
            val childNodeName = childNode.toString()
            if (childNode.childCount > 0 && !packageNode) {
                continue
            } else if (childNode.childCount == 0 && packageNode) {
                insertNode(newNode, parentNode, i, model, reset)
                return newNode
            } else if (newNodeName == childNodeName) {
                return childNode
            } else if (newNodeName < childNodeName) {
                insertNode(newNode, parentNode, i, model, reset)
                return newNode
            }
        }
        insertNode(newNode, parentNode, childCount, model, reset)

        return newNode
    }

    protected fun String.stripClassSuffix(): String = this.substring(0, this.length - CLASSFILE_SUFFIX.length)

    protected fun addEntry(path: String, moduleName: String?, classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        val pathComponents = path.stripClassSuffix().replace('\\', '/').split(Regex("/"))
        if (!path.endsWith(MODULE_INFO_CLASS_FILE_NAME)) {
            addEntry(classPathModel, pathComponents, reset)
        }
        addEntry(modulePathModel, listOf(moduleName ?: UNNAMED_MODULE) + pathComponents, reset)
    }

    private fun addEntry(model: DefaultTreeModel, pathComponents: List<String>, reset: Boolean) {
        var currentNode = model.root as ClassTreeNode
        pathComponents.forEachIndexed { i, pathComponent ->
            currentNode = addOrFindNode(pathComponent, currentNode, i < pathComponents.size - 1, model, reset)
        }
    }

    private fun insertNode(newNode: ClassTreeNode,
                           parentNode: ClassTreeNode,
                           insertionIndex: Int,
                           model: DefaultTreeModel,
                           reset: Boolean) {
        parentNode.insert(newNode, insertionIndex)
        if (!reset) {
            model.nodesWereInserted(parentNode, intArrayOf(insertionIndex))
        }
    }

    fun addToClassPath(classpath: MutableList<ClasspathEntry>): Boolean {
        return if (!classpath.contains(this)) {
            classpath.add(this)
            true
        } else {
            false
        }
    }

    protected fun getModuleName(inputStream: InputStream): String? {
        inputStream.use {
            try {
                val classFile = ClassFileReader.readFromInputStream(inputStream)
                val moduleAttribute = classFile.findAttribute(ModuleAttribute::class.java)
                if (moduleAttribute != null) {
                    return classFile.getConstantPoolEntryName(moduleAttribute.moduleNameIndex)
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

    companion object {
        const val CLASSFILE_SUFFIX = ".class"
        const val UNNAMED_MODULE = "<unnamed module>"
        const val MODULE_INFO_CLASS_FILE_NAME = "module-info.class"

        fun create(element : Element) : ClasspathEntry? = when (element.nodeName) {
            ClasspathDirectoryEntry.NODE_NAME -> ClasspathDirectoryEntry.create(element)
            ClasspathArchiveEntry.NODE_NAME -> ClasspathArchiveEntry.create(element)
            else -> null
        }

        fun getClassPathClassName(className: String, modulePathSelection: Boolean) = if (modulePathSelection) className.substringAfter("/") else className
        fun getModuleName(className: String) = className.substringBefore("/")
    }

}
