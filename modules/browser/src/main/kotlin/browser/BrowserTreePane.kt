/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.structures.*
import org.gjt.jclasslib.structures.Annotation
import org.gjt.jclasslib.structures.attributes.*
import org.gjt.jclasslib.structures.constants.ConstantPlaceholder
import org.gjt.jclasslib.structures.elementvalues.*
import org.gjt.jclasslib.util.*
import org.jetbrains.annotations.Nls
import java.awt.BorderLayout
import java.awt.Dimension
import java.util.*
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel

class BrowserTreePane(private val services: BrowserServices) : JPanel() {

    private val categoryToPath = EnumMap<NodeType, TreePath>(NodeType::class.java)

    val tree: JTree = treeFactory(buildTreeModel()).apply {
        selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        isRootVisible = false
        showsRootHandles = true
        transferHandler = BrowserNodeTransferHandler(services)
        cellRenderer = treeCellRendererFactory().apply {
            treeIcons[TreeIcon.CLOSED]?.apply { closedIcon = this }
            treeIcons[TreeIcon.OPEN]?.apply { openIcon = this }
            treeIcons[TreeIcon.LEAF]?.apply { leafIcon = this }
        }
        if (treeRowHeight > 0) {
            rowHeight = treeRowHeight
        }
    }

    private val scrollPane = scrollPaneFactory(tree)

    init {
        layout = BorderLayout()
        add(scrollPane.apply {
            minimumSize = Dimension(100, 150)
            preferredSize = Dimension(250, 150)
            border = null
        }, BorderLayout.CENTER)
    }

    val root: BrowserTreeNode
        get() = tree.model.root as BrowserTreeNode

    private val treeModel: DefaultTreeModel get() = tree.model as DefaultTreeModel

    fun getPathForCategory(category: NodeType): TreePath = categoryToPath[category]!!

    @Suppress("unused")
    fun showMethod(methodName: String, methodSignature: String) {
        val methodsPath = categoryToPath[NodeType.METHOD] ?: return
        val methodsNode = methodsPath.lastPathComponent as TreeNode

        for (treeNode in methodsNode.children().iterator()) {
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
        return if (codeNode != null) {
            path.pathByAddingChild(codeNode)
        } else {
            path
        }
    }

    fun rebuild() {
        categoryToPath.clear()
        tree.apply {
            clearSelection()
            model = buildTreeModel()
        }
    }

    fun refreshNodes() {
        root.depthFirstEnumeration().asIterator().forEach { node ->
            if (node is RefreshableBrowserTreeNode) {
                node.refresh()
                treeModel.nodeChanged(node)
            }
        }
    }

    fun refresh() {
        enlargeConstantPool()
    }

    private fun enlargeConstantPool() {
        val constantPoolNode = requireNotNull(categoryToPath[NodeType.CONSTANT_POOL_ENTRY]).lastPathComponent as BrowserTreeNode
        val classFile = services.classFile
        val constantPool = classFile.constantPool
        val constantPoolCount = constantPool.size - 2
        val addedIndices = constantPoolNode.childCount..constantPoolCount
        for (i in addedIndices) {
            val constantPoolIndex = i + 1
            constantPoolNode.addConstantPoolEntryNode(constantPoolIndex, classFile.getConstantPoolEntry(constantPoolIndex, Constant::class), constantPool)
        }
        treeModel.nodesWereInserted(constantPoolNode, addedIndices.toList().toIntArray())
    }

    private fun buildTreeModel() = DefaultTreeModel(BrowserRootNode().apply {
        add(NodeType.GENERAL, BrowserTreeNode(getString("tree.general.information"), NodeType.GENERAL, services.classFile))
        add(NodeType.CONSTANT_POOL_ENTRY, buildConstantPoolNode())
        add(NodeType.INTERFACE, buildInterfacesNode())
        add(NodeType.FIELD, buildFieldsNode())
        add(NodeType.METHOD, buildMethodsNode())
        add(NodeType.ATTRIBUTE, buildAttributesNode())
    })

    private inner class BrowserRootNode : BrowserTreeNode(getString("tree.class.file"), NodeType.NO_CONTENT) {
        fun add(nodeType: NodeType, node: BrowserTreeNode) {
            add(node)
            categoryToPath[nodeType] = TreePath(arrayOf<Any>(this, node))
        }
    }

    private fun buildConstantPoolNode(): BrowserTreeNode {
        val constantPool = services.classFile.constantPool
        return BrowserTreeNode(getString("tree.constant.pool"), NodeType.CONSTANT_POOL, constantPool).apply {
            constantPool.forEachIndexed { i, constant ->
                addConstantPoolEntryNode(i, constant, constantPool)
            }
        }
    }

    private fun BrowserTreeNode.addConstantPoolEntryNode(i: Int, constant: Constant, constantPool: Array<Constant>) {
        if (i > 0) {
            add(if (constant == ConstantPlaceholder) {
                BrowserTreeNode(getFormattedIndex(i, constantPool.size) + getString("tree.large.numeric.suffix"), NodeType.NO_CONTENT, constant)
            } else {
                try {
                    BrowserTreeNode(getFormattedIndex(i, constantPool.size) + constant.constantType.verbose, NodeType.CONSTANT_POOL_ENTRY, constant)
                } catch (_: InvalidByteCodeException) {
                    buildNullNode()
                }
            })
        }
    }

    private fun buildInterfacesNode() = BrowserTreeNode(getString("tree.interfaces"), NodeType.INTERFACES).apply {
        services.classFile.interfaces.forEachIndexed { i, interfaceIndex ->
            add(BrowserTreeNode(getString("tree.interface", i), NodeType.INTERFACE, interfaceIndex))
        }
    }

    private fun buildFieldsNode(): BrowserTreeNode =
        buildClassMembersNode(getString("tree.fields"), NodeType.FIELDS, NodeType.FIELD, services.classFile.fields)

    private fun buildMethodsNode(): BrowserTreeNode =
        buildClassMembersNode(getString("tree.methods"), NodeType.METHODS, NodeType.METHOD, services.classFile.methods)

    private fun buildClassMembersNode(@Nls text: String, containerType: NodeType, childType: NodeType, classMembers: Array<out ClassMember>) =
        BrowserTreeNode(text, containerType, classMembers).apply {
            val classMembersCount = classMembers.size
            classMembers.forEachIndexed { i, classMember ->
                try {
                    add(RefreshableBrowserTreeNode(childType, classMember) {
                        getFormattedIndex(i, classMembersCount) + classMember.name
                    }.apply {
                        addAttributeNodes(classMember)
                    })
                } catch (_: InvalidByteCodeException) {
                    add(buildNullNode())
                }
            }
        }

    private fun buildAttributesNode() = BrowserTreeNode(getString("tree.attributes"), NodeType.ATTRIBUTES).apply {
        addAttributeNodes(services.classFile)
    }

    @Suppress("HardCodedStringLiteral")
    private fun buildNullNode() = BrowserTreeNode("[error] null", NodeType.NO_CONTENT)

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
                    is RecordAttribute -> addRecordEntries(attribute)
                    is RuntimeAnnotationsAttribute -> addRuntimeAnnotations(attribute)
                    is RuntimeParameterAnnotationsAttribute -> addRuntimeParameterAnnotation(attribute)
                    is AnnotationDefaultAttribute -> addSingleElementValueEntryNode(attribute.defaultValue, 0, 1)
                    is RuntimeTypeAnnotationsAttribute -> addRuntimeTypeAnnotation(attribute)
                    is AttributeContainer -> addAttributeNodes(attribute)
                }
            })
        } catch (_: InvalidByteCodeException) {
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

    private fun BrowserTreeNode.addRuntimeAnnotations(attribute: RuntimeAnnotationsAttribute) {
        val annotations = attribute.runtimeAnnotations
        annotations.forEachIndexed { i, annotation ->
            addSingleAnnotationNode(annotation, i, annotations.size)
        }
    }

    private fun BrowserTreeNode.addRecordEntries(attribute: RecordAttribute) {
        val entries = attribute.entries
        entries.forEachIndexed { i, entry ->
            addSingleRecordEntry(entry, i, entries.size)
        }
    }

    private fun BrowserTreeNode.addSingleRecordEntry(entry: RecordEntry, index: Int, entriesCount: Int) {
        add(RefreshableBrowserTreeNode(NodeType.RECORD_ENTRY, entry) {
            getFormattedIndex(index, entriesCount) + services.classFile.getConstantPoolEntryName(entry.nameIndex)
        }.apply {
            addAttributeNodes(entry)
        })
    }

    private fun BrowserTreeNode.addRuntimeParameterAnnotation(attribute: RuntimeParameterAnnotationsAttribute) {
        val parameterAnnotations = attribute.parameterAnnotations
        parameterAnnotations.forEachIndexed { i, annotation ->
            addParameterAnnotationNode(annotation, i, parameterAnnotations.size)
        }
    }

    private fun BrowserTreeNode.addParameterAnnotationNode(parameterAnnotations: ParameterAnnotations, index: Int, parameterAnnotationsCount: Int) {
        val name = getFormattedIndex(index, parameterAnnotationsCount) + getString("tree.parameter.annotation")
        add(BrowserTreeNode(name, NodeType.NO_CONTENT, parameterAnnotations).apply {
            val annotations = parameterAnnotations.runtimeAnnotations
            annotations.forEachIndexed { i, annotation ->
                addSingleAnnotationNode(annotation, i, annotations.size)
            }
        })
    }

    private fun BrowserTreeNode.addSingleAnnotationNode(annotation: Annotation, index: Int, attributesCount: Int) {
        val name = getFormattedIndex(index, attributesCount) + getString("tree.annotation")
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
        entries.forEachIndexed { i, ev ->
            addSingleElementValueEntryNode(ev, i, entries.size)
        }
    }

    private fun BrowserTreeNode.addSingleElementValuePairEntryNode(elementValuePair: ElementValuePair, index: Int, attributesCount: Int) {
        val name = getFormattedIndex(index, attributesCount) + elementValuePair.entryName
        add(BrowserTreeNode(name, NodeType.ELEMENT_VALUE_PAIR, elementValuePair).apply {
            addSingleElementValueEntryNode(elementValuePair.elementValue, 0, 1)
        })
    }

    private fun BrowserTreeNode.addSingleElementValueEntryNode(elementValue: ElementValue, index: Int, attributesCount: Int) {
        val prefix = if (attributesCount > 1) getFormattedIndex(index, attributesCount) else ""
        val nodeType = when (elementValue) {
            is AnnotationElementValue -> NodeType.ANNOTATION
            is ArrayElementValue -> NodeType.ARRAY_ELEMENT_VALUE
            is ConstElementValue -> NodeType.CONST_ELEMENT_VALUE
            is ClassElementValue -> NodeType.CLASS_ELEMENT_VALUE
            is EnumElementValue -> NodeType.ENUM_ELEMENTVALUE
            else -> NodeType.GENERIC_ELEMENT_VALUE
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
