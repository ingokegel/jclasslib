/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.ClassAttributeHyperlinkListener
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute
import org.gjt.jclasslib.util.ExtendedJLabel
import org.gjt.jclasslib.util.GUIHelper
import java.awt.*
import java.awt.event.MouseListener
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.tree.TreePath

abstract class KeyValueDetailPane<T : Any>(elementClass: Class<T>, services: BrowserServices) : DetailPane<T>(elementClass, services) {

    private val scrollPane = JScrollPane(this).apply {
        GUIHelper.setDefaultScrollBarUnits(this)
        border = null
    }
    private var currentY = 0

    private val labelToMouseListener = HashMap<ExtendedJLabel, MouseListener>()

    public override val wrapper: JComponent
        get() = scrollPane

    open protected fun addKeyValue(keyValue: KeyValue<T, *>) {
        add(keyValue.keyLabel as JComponent, gc() {
            insets = Insets(1, 10, 0, 10)
        })
        val valueLabel = keyValue.valueLabel
        val commentLabel = keyValue.commentLabel
        add(valueLabel, gc() {
            gridx = 1
            insets = Insets(1, 0, 0, 5)
            if (commentLabel == null) {
                gridwidth = 2
            }
        })
        if (commentLabel != null) {
            add(commentLabel, gc() {
                gridx = 2
                insets = Insets(1, 0, 0, 5)
                fill = GridBagConstraints.HORIZONTAL
            })
            if (commentLabel is ExtendedJLabel) {
                commentLabel.autoTooltip = true
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

    protected val showHandlers = ArrayList<(element: T) -> Unit>()
    protected var element: T? = null

    protected fun addConstantPoolLink(key: String, indexResolver: (element: T) -> Int): DefaultKeyValue<T> {
        val keyValue = DefaultKeyValue<T>(key, linkLabel(), highlightLabel())
        addKeyValue(keyValue)
        showHandlers.add { element ->
            val constantPoolIndex = indexResolver(element)
            keyValue.valueLabel.apply {
                text = CPINFO_LINK_TEXT + constantPoolIndex
                setupMouseListener(ConstantPoolHyperlinkListener(services, constantPoolIndex))
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            }
            keyValue.commentLabel?.applyComment(constantPoolIndex)
        }
        return keyValue
    }

    protected fun addAttributeLink(key: String, attributeClass: Class<BootstrapMethodsAttribute>, prefix: String, indexResolver: (element: T) -> Int): DefaultKeyValue<T> {
        val keyValue = DefaultKeyValue<T>(key, linkLabel())
        addKeyValue(keyValue)
        showHandlers.add { element ->
            val index = indexResolver(element)
            keyValue.valueLabel.apply {
                text = prefix + index
                setupMouseListener(ClassAttributeHyperlinkListener(services, index, attributeClass))
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            }
        }
        return keyValue
    }

    protected fun addDetail(key: String, textResolver: (element: T) -> String): DefaultKeyValue<T> {
        val keyValue = DefaultKeyValue<T>(key, highlightLabel())
        addKeyValue(keyValue)
        showHandlers.add { element ->
            keyValue.valueLabel.text = textResolver(element)
        }
        return keyValue
    }

    protected fun addMultiLineDetail(key: String, textResolver: (element: T) -> String): HtmlKeyValue<T> {
        val keyValue = HtmlKeyValue<T>(key, highlightTextArea())
        addKeyValue(keyValue)
        showHandlers.add { element ->
            keyValue.valueLabel.text = textResolver(element)
            keyValue.show(element)
        }
        return keyValue
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

    private fun ExtendedJLabel.setupMouseListener(mouseListener: MouseListener) {
        labelToMouseListener[this]?.let { removeMouseListener(it) }
        addMouseListener(mouseListener)
        labelToMouseListener.put(this, mouseListener)
    }

    private fun ExtendedJLabel.applyComment(constantPoolIndex: Int) {
        toolTipText = text
        text = "<" + getConstantPoolEntryName(constantPoolIndex) + ">"
    }

}
