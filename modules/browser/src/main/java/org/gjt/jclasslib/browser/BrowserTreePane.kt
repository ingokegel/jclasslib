/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.structures.*
import org.gjt.jclasslib.structures.Annotation
import org.gjt.jclasslib.structures.attributes.*
import org.gjt.jclasslib.structures.constants.ConstantPlaceholder
import org.gjt.jclasslib.structures.elementvalues.AnnotationElementValue
import org.gjt.jclasslib.structures.elementvalues.ArrayElementValue
import org.gjt.jclasslib.structures.elementvalues.ElementValue
import org.gjt.jclasslib.structures.elementvalues.ElementValuePair
import java.awt.BorderLayout
import java.awt.Dimension
import java.util.*
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel

class BrowserTreePane(private val services: BrowserServices) : JPanel() {

    private val categoryToPath = EnumMap<NodeType, TreePath>(NodeType::class.java)

    val tree: JTree = JTree(buildTreeModel()).apply {
        selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        isRootVisible = false
        showsRootHandles = true
        transferHandler = BrowserNodeTransferHandler(services)
    }

    init {
        layout = BorderLayout()
        add(JScrollPane(tree).apply {
            minimumSize = Dimension(100, 150)
            preferredSize = Dimension(250, 150)
        }, BorderLayout.CENTER)
    }

    fun getPathForCategory(category: NodeType): TreePath {
        return categoryToPath[category]!!
    }

    @Suppress("unused")
    fun showMethod(methodName: String, methodSignature: String) {
        val methodsPath = categoryToPath[NodeType.METHOD] ?: return
        val methodsNode = methodsPath.lastPathComponent as TreeNode

        methodsNode.children().iterator().forEach { treeNode ->
            treeNode as BrowserTreeNode
            val method = treeNode.element as MethodInfo
            if (method.name == methodName && method.descriptor.startsWith(methodSignature)) {
                val path = getMethodDisplayPath(methodsPath, method, treeNode)
                tree.apply {
                    makeVisible(path)
                    scrollPathToVisible(path)
                    selectionPath = path
                }
            }
        }
    }

    private fun getMethodDisplayPath(methodsPath: TreePath, method: MethodInfo, treeNode: BrowserTreeNode): TreePath {
        val path = methodsPath.pathByAddingChild(treeNode)
        val codeNode = findCodeNode(treeNode, method)
        if (codeNode != null) {
            return path.pathByAddingChild(codeNode)
        } else {
            return path
        }
    }

    fun rebuild() {
        categoryToPath.clear()
        tree.apply {
            clearSelection()
            model = buildTreeModel()
        }
    }

    private fun buildTreeModel() = DefaultTreeModel(BrowserRootNode().apply {
        add(NodeType.GENERAL, BrowserTreeNode("General Information", NodeType.GENERAL))
        add(NodeType.CONSTANT_POOL, buildConstantPoolNode())
        add(NodeType.INTERFACE, buildInterfacesNode())
        add(NodeType.FIELD, buildFieldsNode())
        add(NodeType.METHOD, buildMethodsNode())
        add(NodeType.ATTRIBUTE, buildAttributesNode())
    })

    private inner class BrowserRootNode : BrowserTreeNode("Class file") {
        fun add(nodeType: NodeType, node: BrowserTreeNode) {
            add(node)
            categoryToPath.put(nodeType, TreePath(arrayOf<Any>(this, node)))
        }
    }

    private fun buildConstantPoolNode() = BrowserTreeNode("Constant Pool").apply {
        val constantPool = services.classFile.constantPool
        val constantPoolCount = constantPool.size
        constantPool.forEachIndexed { i, constant ->
            if (i > 0) {
                add(if (constant is ConstantPlaceholder) {
                    BrowserTreeNode(getFormattedIndex(i, constantPoolCount) + "(large numeric continued)", NodeType.NO_CONTENT)
                } else {
                    try {
                        BrowserTreeNode(getFormattedIndex(i, constantPoolCount) + constant.constantType.verbose, NodeType.CONSTANT_POOL, constant)
                    } catch (ex: InvalidByteCodeException) {
                        buildNullNode()
                    }
                })
            }
        }
    }

    private fun buildInterfacesNode() = BrowserTreeNode("Interfaces").apply {
        services.classFile.interfaces.forEachIndexed { i, interfaceIndex ->
            add(BrowserTreeNode("Interface " + i, NodeType.INTERFACE, interfaceIndex))
        }
    }

    private fun buildFieldsNode(): BrowserTreeNode =
            buildClassMembersNode("Fields", NodeType.FIELDS, NodeType.FIELD, services.classFile.fields)

    private fun buildMethodsNode(): BrowserTreeNode =
            buildClassMembersNode("Methods", NodeType.METHODS, NodeType.METHOD, services.classFile.methods)

    private fun buildClassMembersNode(text: String, containerType: NodeType, childType: NodeType, classMembers: Array<out ClassMember>) =
            BrowserTreeNode(text, containerType, classMembers).apply {
                val classMembersCount = classMembers.size
                classMembers.forEachIndexed { i, classMember ->
                    try {
                        val name = getFormattedIndex(i, classMembersCount) + classMember.name
                        add(BrowserTreeNode(name, childType, classMember).apply {
                            addAttributeNodes(classMember)
                        })
                    } catch (ex: InvalidByteCodeException) {
                        add(buildNullNode())
                    }
                }
            }

    private fun buildAttributesNode() = BrowserTreeNode("Attributes").apply {
        addAttributeNodes(services.classFile)
    }

    private fun buildNullNode() = BrowserTreeNode("[error] null")

    private fun BrowserTreeNode.addAttributeNodes(structure: AttributeContainer) {
        val attributes = structure.attributes
        attributes.forEachIndexed { i, attributeInfo ->
            addSingleAttributeNode(attributeInfo, i, attributes.size)
        }
    }

    private fun BrowserTreeNode.addSingleAttributeNode(attribute: AttributeInfo, index: Int, attributesCount: Int) {
        try {
            val name = getFormattedIndex(index, attributesCount) + attribute.name
            add(BrowserTreeNode(name, NodeType.ATTRIBUTE, attribute).apply {
                when (attribute) {
                    is RuntimeAnnotationsAttribute -> addRuntimeAnnotations(attribute)
                    is RuntimeParameterAnnotationsAttribute -> addRuntimeParameterAnnotation(attribute)
                    is AnnotationDefaultAttribute -> addSingleElementValueEntryNode(attribute.defaultValue, 0, 1)
                    is RuntimeTypeAnnotationsAttribute -> addRuntimeTypeAnnotation(attribute)
                    is AttributeContainer -> addAttributeNodes(attribute)
                }
            })
        } catch (ex: InvalidByteCodeException) {
            add(buildNullNode())
        }
    }

    private fun getFormattedIndex(index: Int, maxIndex: Int): String {
        val paddedIndex = index.toString().padStart((maxIndex - 1).toString().length, '0')
        return "[$paddedIndex] "
    }

    private fun findCodeNode(treeNode: BrowserTreeNode, methodInfo: MethodInfo): BrowserTreeNode? {
        return methodInfo.attributes.indexOfFirst { it is CodeAttribute }.let { index ->
            when (index) {
                -1 -> null
                else -> treeNode.getChildAt(index) as BrowserTreeNode
            }
        }
    }

    private fun BrowserTreeNode.addRuntimeAnnotations(attribute : RuntimeAnnotationsAttribute) {
        val annotations = attribute.runtimeAnnotations
        annotations.forEachIndexed { i, annotation ->
            addSingleAnnotationNode(annotation, i, annotations.size)
        }
    }

    private fun BrowserTreeNode.addRuntimeParameterAnnotation(attribute: RuntimeParameterAnnotationsAttribute) {
        val parameterAnnotations = attribute.parameterAnnotations
        parameterAnnotations.forEachIndexed { i, annotation ->
            addParameterAnnotationNode(annotation, i, parameterAnnotations.size)
        }
    }

    private fun BrowserTreeNode.addParameterAnnotationNode(parameterAnnotations: ParameterAnnotations, index: Int, parameterAnnotationsCount: Int) {
        val name = getFormattedIndex(index, parameterAnnotationsCount) + "Parameter annotation"
        add(BrowserTreeNode(name, NodeType.NO_CONTENT, parameterAnnotations).apply {
            val annotations = parameterAnnotations.runtimeAnnotations
            annotations.forEachIndexed { i, annotation ->
                addSingleAnnotationNode(annotation, i, annotations.size)
            }
        })
    }

    private fun BrowserTreeNode.addSingleAnnotationNode(annotation: Annotation, index: Int, attributesCount: Int) {
        val name = getFormattedIndex(index, attributesCount) + "Annotation"
        add(BrowserTreeNode(name, NodeType.ANNOTATION, annotation).apply {
            addElementValuePairEntry(annotation)
        })
    }

    private fun BrowserTreeNode.addRuntimeTypeAnnotation(attribute: RuntimeTypeAnnotationsAttribute) {
        val annotations = attribute.runtimeAnnotations
        annotations.forEachIndexed { i, annotation ->
            addSingleTypeAnnotationNode(annotation, i, annotations.size)
        }
    }

    private fun BrowserTreeNode.addSingleTypeAnnotationNode(annotation: TypeAnnotation, index: Int, attributesCount: Int) {
        val name = getFormattedIndex(index, attributesCount) + annotation.targetType.toString()
        add(BrowserTreeNode(name, NodeType.TYPE_ANNOTATION, annotation).apply {
            addSingleAnnotationNode(annotation.annotation, 0, 1)
        })
    }

    private fun BrowserTreeNode.addElementValuePairEntry(annotation: AnnotationData) {
        val entries = annotation.elementValuePairEntries
        entries.forEachIndexed { i, elementValuePair ->
            addSingleElementValuePairEntryNode(elementValuePair, i, entries.size)
        }
    }

    private fun BrowserTreeNode.addArrayElementValueEntry(elementValue: ArrayElementValue) {
        val entries = elementValue.elementValueEntries
        entries.forEachIndexed { i, elementValue ->
            addSingleElementValueEntryNode(elementValue, i, entries.size)
        }
    }

    private fun BrowserTreeNode.addSingleElementValuePairEntryNode(elementValuePair: ElementValuePair, index: Int, attributesCount: Int) {
        val name = getFormattedIndex(index, attributesCount) + elementValuePair.entryName
        add(BrowserTreeNode(name, NodeType.ELEMENTVALUEPAIR, elementValuePair).apply {
            addSingleElementValueEntryNode(elementValuePair.elementValue, 0, 1)
        })
    }

    private fun BrowserTreeNode.addSingleElementValueEntryNode(elementValue: ElementValue, index: Int, attributesCount: Int) {
        val prefix = if (attributesCount > 1) getFormattedIndex(index, attributesCount) else ""
        val nodeType = when (elementValue) {
            is AnnotationElementValue -> NodeType.ANNOTATION
            is ArrayElementValue -> NodeType.ARRAYELEMENTVALUE
            else -> NodeType.ELEMENTVALUE
        }
        add(BrowserTreeNode(prefix + elementValue.entryName, nodeType, elementValue).apply {
            if (elementValue is AnnotationElementValue) {
                addElementValuePairEntry(elementValue)
            } else if (elementValue is ArrayElementValue) {
                addArrayElementValueEntry(elementValue)
            }
        })
    }
}
