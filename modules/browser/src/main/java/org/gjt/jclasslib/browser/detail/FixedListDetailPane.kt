/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.AbstractDetailPane
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute
import org.gjt.jclasslib.util.ExtendedJLabel
import org.gjt.jclasslib.util.GUIHelper
import org.gjt.jclasslib.util.TextDisplay

import javax.swing.*
import javax.swing.tree.TreePath
import java.awt.*
import java.util.ArrayList

abstract class FixedListDetailPane<T : Any>(private val elementClass: Class<T>, services: BrowserServices) : AbstractDetailPane(services) {

    private val scrollPane = JScrollPane(this).apply {
        GUIHelper.setDefaultScrollBarUnits(this)
        border = null
    }
    private var currentY = 0

    public override val wrapper: JComponent
        get() = scrollPane

    @JvmOverloads open protected fun addDetailPaneEntry(key: ExtendedJLabel, value: TextDisplay?, comment: TextDisplay? = null) {
        add(key, gc() {
            insets = Insets(1, 10, 0, 10)
        })
        if (value != null) {
            add(value as JComponent, gc() {
                gridx = 1
                insets = Insets(1, 0, 0, 5)
                if (comment == null) {
                    gridwidth = 2
                }
            })
        }
        if (comment != null) {
            add(comment as JComponent, gc() {
                if (value == null) {
                    gridx = 1
                    gridwidth = 2
                } else {
                    gridx = 2
                }
                insets = Insets(1, 0, 0, 5)
                fill = GridBagConstraints.HORIZONTAL
            })
            if (comment is ExtendedJLabel) {
                comment.autoTooltip = true
            }
        }
        nextLine()
    }

    protected fun nextLine() {
        currentY++
    }

    override fun setupComponent() {
        layout = GridBagLayout()
        addLabels()
        add(JPanel(), gc() {
            gridx = 2
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
        })
    }

    protected fun gc(config: GridBagConstraints.() -> Unit) = GridBagConstraints().apply {
        anchor = GridBagConstraints.NORTHWEST
        gridy = currentY
        config()
    }

    override fun show(treePath: TreePath) {
        scrollPane.viewport.viewPosition = Point(0, 0)
        element = getElement(treePath)
        element?.let { element -> showHandlers.forEach { it.invoke(element) } }
    }

    protected abstract fun addLabels()

    protected open fun addSpecial(gridy: Int): Int {
        return 0
    }

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

    protected fun addMultiLineDetail(name: String, textResolver: (element: T) -> String) : LineControl<T> {
        val nameLabel = highlightTextArea()
        val keyLabel = normalLabel(name)
        addDetailPaneEntry(keyLabel, nameLabel)
        // TODO use multiline details and line control for all cases
        val lineControl = LineControl<T>(keyLabel, nameLabel)
        showHandlers.add { element ->
            nameLabel.text = textResolver(element)
            lineControl.show(element)
        }
        return lineControl
    }

    protected fun addClassElementOpener(constantResolver: (element: T) -> Constant) {
        if (services.canOpenClassFiles()) {
            val classElementOpener = ClassElementOpener(this)
            add(classElementOpener, gc() {
                weightx = 1.0
                anchor = GridBagConstraints.WEST
                insets = Insets(5, 10, 0, 10)
                gridx = 0
                gridwidth = 3
            })
            nextLine()
            showHandlers.add { element ->
                classElementOpener.setConstant(constantResolver(element))
            }
        }
    }
}
