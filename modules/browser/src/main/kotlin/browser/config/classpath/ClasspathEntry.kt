/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.structures.attributes.ModuleAttribute
import org.w3c.dom.Element
import java.io.InputStream
import javax.swing.tree.DefaultTreeModel

abstract class ClasspathEntry : ClasspathComponent {

    abstract fun saveWorkspace(element: Element)

    override fun contains(component: ClasspathComponent): Boolean = false

    protected fun addOrFindNode(newNodeName: String,
                                parentNode: ClassTreeNode,
                                packageNode: Boolean,
                                model: DefaultTreeModel,
                                reset: Boolean): ClassTreeNode {
        val childCount = parentNode.childCount
        if (childCount == 0) {
            return insertNewNode(newNodeName, packageNode, parentNode, 0, model, reset)
        }

        // Find the boundary between package nodes and class leaf nodes using binary search
        var lo = 0
        var hi = childCount
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if ((parentNode.getChildAt(mid) as ClassTreeNode).isPackageNode) {
                lo = mid + 1
            } else {
                hi = mid
            }
        }
        val packageEnd = lo

        // Binary search within the relevant range (packages or classes)
        val searchStart: Int
        val searchEnd: Int
        if (packageNode) {
            searchStart = 0
            searchEnd = packageEnd
        } else {
            searchStart = packageEnd
            searchEnd = childCount
        }

        lo = searchStart
        hi = searchEnd
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            val cmp = newNodeName.compareTo((parentNode.getChildAt(mid) as ClassTreeNode).userObject as String)
            when {
                cmp < 0 -> hi = mid
                cmp > 0 -> lo = mid + 1
                else -> return parentNode.getChildAt(mid) as ClassTreeNode
            }
        }
        return insertNewNode(newNodeName, packageNode, parentNode, lo, model, reset)
    }

    private fun insertNewNode(name: String, packageNode: Boolean, parentNode: ClassTreeNode, index: Int, model: DefaultTreeModel, reset: Boolean): ClassTreeNode =
        ClassTreeNode(name, packageNode).also {
            insertNode(it, parentNode, index, model, reset)
        }

    protected fun String.stripClassSuffix(): String = this.removeSuffix(CLASSFILE_SUFFIX)

    protected fun addEntry(path: String, moduleName: String?, classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        val pathComponents = path.stripClassSuffix().replace('\\', '/').split("/")
        if (!path.endsWith(MODULE_INFO_CLASS_FILE_NAME)) {
            addEntry(classPathModel, pathComponents, reset)
        }
        addEntry(modulePathModel, buildList(pathComponents.size + 1) {
            add(moduleName ?: UNNAMED_MODULE)
            addAll(pathComponents)
        }, reset)
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
                val moduleAttribute = classFile.findAttribute(ModuleAttribute::class)
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
