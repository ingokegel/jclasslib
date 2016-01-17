/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import java.io.File
import java.io.IOException
import javax.swing.tree.DefaultTreeModel

abstract class ClasspathEntry : ClasspathComponent {

    var fileName: String? = null
        set(fileName) {
            field = fileName
            try {
                file = File(fileName).canonicalFile
            } catch (e: IOException) {
                file = null
            }
        }

    protected var file: File? = null
        private set

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other?.javaClass != javaClass) {
            return false
        }
        other as ClasspathEntry

        return fileName == other.fileName
    }

    override fun hashCode(): Int {
        return fileName?.hashCode() ?: 0
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
            } else if (newNodeName.compareTo(childNodeName) < 0) {
                insertNode(newNode, parentNode, i, model, reset)
                return newNode
            }
        }
        insertNode(newNode, parentNode, childCount, model, reset)

        return newNode
    }

    protected fun stripClassSuffix(name: String): String {
        return name.substring(0, name.length - CLASSFILE_SUFFIX.length)
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
        @JvmField
        protected val CLASSFILE_SUFFIX = ".class"
    }

}
