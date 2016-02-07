/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute
import java.util.*
import javax.swing.tree.TreePath

abstract class TypedDetailPane<T : Any>(
        private val elementClass: Class<T>, services: BrowserServices) : FixedListDetailPane(services) {

    protected val showHandlers = ArrayList<(element: T) -> Unit>()
    protected var element: T? = null

    override fun getElement(treePath: TreePath): T {
        return elementClass.cast(super.getElement(treePath))
    }

    protected fun addConstantPoolLink(name: String, indexResolver: (element: T) -> Int) {
        val nameLabel = linkLabel()
        val nameVerboseLabel = highlightLabel()
        addDetailPaneEntry(normalLabel(name), nameLabel, nameVerboseLabel)
        showHandlers.add { element ->
            constantPoolHyperlink(nameLabel, nameVerboseLabel, indexResolver(element))
        }
    }

    protected fun addAttributeLink(name: String, attributeClass: Class<BootstrapMethodsAttribute>, prefix: String, indexResolver: (element: T) -> Int) {
        val nameLabel = linkLabel()
        addDetailPaneEntry(normalLabel(name), nameLabel)
        showHandlers.add { element ->
            classAttributeIndexHyperlink(nameLabel, null, indexResolver(element), attributeClass, prefix)
        }
    }

    protected fun addCompositeDetail(name: String, textResolver: (element: T) -> Pair<String, String>) {
        val nameLabel = linkLabel()
        val nameVerboseLabel = highlightLabel()
        addDetailPaneEntry(normalLabel(name), nameLabel, nameVerboseLabel)
        showHandlers.add { element ->
            val texts = textResolver(element)
            nameLabel.text = texts.first
            nameVerboseLabel.text = texts.second
        }
    }

    protected fun addDetail(name: String, textResolver: (element: T) -> String) {
        val nameLabel = highlightLabel()
        addDetailPaneEntry(normalLabel(name), nameLabel)
        showHandlers.add { element ->
            nameLabel.text = textResolver(element)
        }
    }

    override fun show(treePath: TreePath) {
        super.show(treePath)
        element = getElement(treePath)
        element?.let { element -> showHandlers.forEach { it.invoke(element) } }
    }
}


