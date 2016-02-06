/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute
import java.awt.GridBagConstraints
import java.awt.Insets
import java.util.*
import javax.swing.tree.TreePath

abstract class AbstractConstantInfoDetailPane<T : Constant>(
        private val constantClass: Class<T>, services: BrowserServices) : FixedListDetailPane(services) {

    private val showHandlers = ArrayList<(constant: T) -> Unit>()

    protected fun getConstant(treePath: TreePath): T {
        return constantClass.cast(getElement(treePath))
    }

    protected fun addConstantPoolLink(name: String, indexResolver: (constant: T) -> Int) {
        val nameLabel = linkLabel()
        val nameVerboseLabel = highlightLabel()
        addDetailPaneEntry(normalLabel(name), nameLabel, nameVerboseLabel)
        showHandlers.add { constant ->
            constantPoolHyperlink(nameLabel, nameVerboseLabel, indexResolver(constant))
        }
    }

    protected fun addAttributeLink(name: String, attributeClass: Class<BootstrapMethodsAttribute>, prefix: String, indexResolver: (constant: T) -> Int) {
        val nameLabel = linkLabel()
        addDetailPaneEntry(normalLabel(name), nameLabel)
        showHandlers.add { constant ->
            classAttributeIndexHyperlink(nameLabel, null, indexResolver(constant), attributeClass, prefix)
        }
    }

    protected fun addCompositeDetail(name: String, textResolver: (constant: T) -> Pair<String, String>) {
        val nameLabel = linkLabel()
        val nameVerboseLabel = highlightLabel()
        addDetailPaneEntry(normalLabel(name), nameLabel, nameVerboseLabel)
        showHandlers.add { constant ->
            val texts = textResolver(constant)
            nameLabel.text = texts.first
            nameVerboseLabel.text = texts.second
        }
    }

    protected fun addDetail(name: String, textResolver: (constant: T) -> String) {
        val nameLabel = highlightLabel()
        addDetailPaneEntry(normalLabel(name), nameLabel)
        showHandlers.add { constant ->
            nameLabel.text = textResolver(constant)
        }
    }

    protected fun addClassElementOpener() {
        if (services.canOpenClassFiles()) {
            val classElementOpener = ClassElementOpener(this)
            add(classElementOpener, gc() {
                weightx = 1.0
                anchor = GridBagConstraints.WEST
                insets = Insets(5, 10, 0, 10)
                gridx = 0
                gridwidth = 3
            })
            currentY++
            showHandlers.add { constant ->
                classElementOpener.setConstant(constant)
            }
        }
    }

    override fun show(treePath: TreePath) {
        super.show(treePath)
        val constant = getConstant(treePath)
        showHandlers.forEach { it.invoke(constant) }
    }
}


