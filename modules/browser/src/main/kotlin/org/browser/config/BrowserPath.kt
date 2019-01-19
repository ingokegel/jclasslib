/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config

import kotlinx.dom.build.addElement
import kotlinx.dom.childElements
import kotlinx.dom.firstChildElement
import org.gjt.jclasslib.browser.BrowserTreeNode
import org.gjt.jclasslib.browser.NodeType
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassMember
import org.w3c.dom.Element
import java.util.*

class BrowserPath {
    val pathComponents = LinkedList<PathComponent>()
    fun addPathComponent(pathComponent: PathComponent) {
        pathComponents.add(pathComponent)
    }

    fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME) {
            pathComponents.forEach {
                it.saveWorkspace(this)
            }
        }
    }

    companion object {
        const val NODE_NAME = "path"

        fun create(element: Element): BrowserPath? {
            return element.firstChildElement(NODE_NAME)?.let { pathElement ->
                BrowserPath().apply {
                    pathElement.childElements().forEach { pathComponentElement ->
                        PathComponent.create(pathComponentElement)?.let { pathComponent ->
                            this.pathComponents.add(pathComponent)
                        }
                    }
                }
            }
        }
    }
}


interface PathComponent {
    fun saveWorkspace(element: Element)
    fun matches(node: BrowserTreeNode): Boolean

    companion object {
        fun create(element: Element): PathComponent? = when (element.nodeName) {
            CategoryHolder.NODE_NAME -> CategoryHolder.create(element)
            IndexHolder.NODE_NAME -> IndexHolder.create(element)
            ReferenceHolder.NODE_NAME -> ReferenceHolder.create(element)
            AttributeHolder.NODE_NAME -> AttributeHolder.create(element)
            else -> null
        }
    }

}

data class CategoryHolder(val category: NodeType = NodeType.NO_CONTENT) : PathComponent {
    override fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME) {
            setAttribute(ATTRIBUTE_NAME, category.name)
        }
    }

    override fun matches(node: BrowserTreeNode) = node.type == category

    companion object {
        const val NODE_NAME = "category"
        private const val ATTRIBUTE_NAME = "name"

        fun create(element: Element): CategoryHolder? {
            val nodeType = NodeType.getByName(element.getAttribute(ATTRIBUTE_NAME))
            return nodeType?.let { CategoryHolder(nodeType) }
        }
    }
}

data class IndexHolder(val index: Int = -1) : PathComponent {
    override fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME) {
            setAttribute(ATTRIBUTE_INDEX, index.toString())
        }
    }

    override fun matches(node: BrowserTreeNode) = node.index == index

    companion object {
        const val NODE_NAME = "element"
        private const val ATTRIBUTE_INDEX = "index"

        fun create(element: Element) = IndexHolder(element.getAttribute(ATTRIBUTE_INDEX).toInt())
    }
}

data class ReferenceHolder(val name: String = "", val type: String = "") : PathComponent {
    override fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME) {
            setAttribute(ATTRIBUTE_NAME, name)
            setAttribute(ATTRIBUTE_TYPE, type)
        }
    }

    override fun matches(node: BrowserTreeNode) = (node.element as ClassMember).let { classMember ->
        classMember.name == name && classMember.descriptor == type
    }

    companion object {
        const val NODE_NAME = "reference"
        private const val ATTRIBUTE_NAME = "name"
        private const val ATTRIBUTE_TYPE = "type"

        fun create(element: Element) = ReferenceHolder(element.getAttribute(ATTRIBUTE_NAME), element.getAttribute(ATTRIBUTE_TYPE))
    }
}

data class AttributeHolder(val name: String) : PathComponent {
    override fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME) {
            setAttribute(ATTRIBUTE_NAME, name)
        }
    }

    override fun matches(node: BrowserTreeNode) = (node.element as AttributeInfo).name == name

    companion object {
        const val NODE_NAME = "attribute"
        private const val ATTRIBUTE_NAME = "name"

        fun create(element: Element) = AttributeHolder(element.getAttribute(ATTRIBUTE_NAME))
    }
}