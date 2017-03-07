/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import org.w3c.dom.Element
import java.io.File
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

    override fun hashCode(): Int {
        return file.hashCode()
    }

    // classpath entries are immutable
    override fun addClasspathChangeListener(listener: ClasspathChangeListener) {
    }

    override fun removeClasspathChangeListener(listener: ClasspathChangeListener) {
    }

    protected fun addOrFindNode(newNodeName: String,
                                parentNode: ClassTreeNode,
                                packageNode: Boolean,
                                model: DefaultTreeModel,
                                reset: Boolean): ClassTreeNode {
        val childCount = parentNode.childCount

        val newNode = ClassTreeNode(newNodeName, packageNode)
        for (i in 0..childCount - 1) {
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

    protected fun String.stripClassSuffix(): String {
        return this.substring(0, this.length - CLASSFILE_SUFFIX.length)
    }

    protected fun addEntry(path: String, model: DefaultTreeModel, reset: Boolean) {
        val pathComponents = path.stripClassSuffix().replace('\\', '/').split(Regex("/"))
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
        if (!classpath.contains(this)) {
            classpath.add(this)
            return true
        } else {
            return false
        }
    }

    companion object {
        val CLASSFILE_SUFFIX = ".class"

        fun create(element : Element) : ClasspathEntry? = when (element.nodeName) {
            ClasspathDirectoryEntry.NODE_NAME -> ClasspathDirectoryEntry.create(element)
            ClasspathArchiveEntry.NODE_NAME -> ClasspathArchiveEntry.create(element)
            else -> null
        }
    }

}
