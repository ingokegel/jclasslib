/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.BrowserTreeNode
import org.gjt.jclasslib.browser.NodeType
import org.gjt.jclasslib.browser.detail.attributes.CodeAttributeDetailPane
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.Structure
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.structures.attributes.LocalVariableTableAttribute
import org.gjt.jclasslib.structures.attributes.TypeAnnotation
import javax.swing.JTree
import javax.swing.tree.TreePath

class TypeAnnotationDetailPane(services: BrowserServices) : KeyValueDetailPane<TypeAnnotation>(TypeAnnotation::class.java, services) {
    override fun addLabels() {
        addDetail("Target Type:") { typeAnnotation -> typeAnnotation.targetType.toString() }

        addMultiLineHtmlDetail("Target Info:") { typeAnnotation ->
            typeAnnotation.targetInfo.verbose.replace("\n", "<br>")
        }.linkHandler { description ->
            handleLink(description)
        }

        addMultiLineHtmlDetail("Type path:") { typeAnnotation ->
            StringBuilder().apply {
                for (typePathEntry in typeAnnotation.typePathEntries) {
                    append(typePathEntry.typePathKind)
                    append(", argument index ").append(typePathEntry.typeArgumentIndex)
                }
            }.toString()
        }.visibilityPredicate { typeAnnotation ->
            typeAnnotation.typePathEntries.isNotEmpty()
        }
    }

    private fun handleLink(description: String) {
        val type = description[0]
        val index = Integer.parseInt(description.substring(1))
        when (type) {
            'E' -> handleExceptionsLink(index)
            'L' -> handleLocalVarLink(index)
            'I' -> handleInterfaceLink(index)
            else -> throw IllegalArgumentException("Invalid link type " + type)
        }
    }

    private fun handleExceptionsLink(index: Int) {
        val path = findParentNode(CodeAttribute::class.java, tree.selectionPath)
        selectPath(path)
        val detailPane = services.browserComponent.detailPane.currentDetailPane as AttributeDetailPane
        val codeAttributeDetailPane = detailPane.getDetailPane(CodeAttribute::class.java) as CodeAttributeDetailPane
        codeAttributeDetailPane.selectExceptionTableDetailPane()
        codeAttributeDetailPane.exceptionTableDetailPane.selectIndex(index)
    }

    private fun handleLocalVarLink(index: Int) {
        val (path, attribute) = findAttributeViaParent(CodeAttribute::class.java, LocalVariableTableAttribute::class.java)
        handleListLink(getLinkIndex(index, attribute), path, attribute)
    }

    private fun <T : AttributeInfo> findAttributeViaParent(parentClass: Class<out Structure>, attributeClass: Class<T>): Pair<TreePath, T> {
        val path = findAttributeChildNode(findParentNode(parentClass, tree.selectionPath), attributeClass)
        return Pair(path, attributeClass.cast((path.lastPathComponent as BrowserTreeNode).element))
    }

    private fun getLinkIndex(index: Int, attribute: LocalVariableTableAttribute): Int {
        val localVariableTable = attribute.localVariableEntries
        localVariableTable.forEachIndexed { i, localVariableEntry ->
            if (localVariableEntry.index == index) {
                return i
            }
        }
        throw IllegalArgumentException("index $index not found in local variable table")
    }

    private fun handleInterfaceLink(index: Int) {
        val interfacesPath = services.browserComponent.treePane.getPathForCategory(NodeType.INTERFACE)
        val interfacesNode = interfacesPath.lastPathComponent as BrowserTreeNode
        if (index >= interfacesNode.childCount) {
            throw IllegalArgumentException("Invalid interface index " + index)
        }
        val path = interfacesPath.pathByAddingChild(interfacesNode.getChildAt(index))
        selectPath(path)
    }

    private fun handleListLink(index: Int, path: TreePath, attribute: AttributeInfo) {
        selectPath(path)
        val detailPane = services.browserComponent.detailPane.currentDetailPane as AttributeDetailPane
        (detailPane.getDetailPane(attribute::class.java) as TableDetailPane).selectIndex(index)
    }

    private fun selectPath(path: TreePath) {
        tree.apply {
            selectionPath = path
            scrollPathToVisible(path)
        }
    }

    private val tree: JTree
        get() = services.browserComponent.treePane.tree

    private fun findAttributeChildNode(path: TreePath, attributeClass: Class<out AttributeInfo>): TreePath {
        val node = path.lastPathComponent as BrowserTreeNode
        node.children().iterator().forEach { child ->
            val attributeNode = child as BrowserTreeNode
            if (attributeNode.element?.let { it::class.java } == attributeClass) {
                return path.pathByAddingChild(attributeNode)
            }
        }
        throw RuntimeException("No attribute node for class $attributeClass found")
    }

    private fun findParentNode(elementClass: Class<out Any>, path: TreePath?): TreePath {
        if (path == null) {
            throw RuntimeException("No parent node with element class $elementClass found")
        }
        val node = path.lastPathComponent as BrowserTreeNode
        if (node.element?.let { it::class.java } == elementClass) {
            return path
        } else {
            return findParentNode(elementClass, path.parentPath)
        }
    }

}
