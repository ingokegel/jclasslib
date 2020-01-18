/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.*
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute
import org.gjt.jclasslib.util.*
import java.awt.Cursor
import java.awt.Point
import java.awt.event.MouseListener
import java.util.*
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.event.HyperlinkEvent
import javax.swing.tree.TreePath

abstract class KeyValueDetailPane<T : Any>(elementClass: Class<T>, services: BrowserServices) : DetailPane<T>(elementClass, services) {

    private val scrollPane = JScrollPane(this).apply {
        GUIHelper.setDefaultScrollBarUnits(this)
        border = null
    }

    private val labelToMouseListener = HashMap<ExtendedJLabel, MouseListener>()

    public override val wrapper: JComponent
        get() = scrollPane

    private fun addKeyValue(keyValue: KeyValue<T, *>) {
        add(keyValue.keyLabel as JComponent)
        val valueLabel = keyValue.valueLabel
        val commentLabel = keyValue.commentLabel
        add(valueLabel, if (commentLabel == null) "growx, spanx 2" else "")
        if (commentLabel != null) {
            add(commentLabel, "growx")
            if (commentLabel is ExtendedJLabel) {
                commentLabel.autoTooltip = true
            }
        }
    }

    override fun setupComponent() {
        layout = MigLayout("wrap" + if (hasInsets()) "" else ", insets 0", "[][][grow]")
        addLabels()
    }

    protected open fun hasInsets() = false

    override fun show(treePath: TreePath) {
        scrollPane.viewport.viewPosition = Point(0, 0)
        runShowHandlers(treePath)
    }

    private fun runShowHandlers(treePath: TreePath) {
        element = getElement(treePath)
        element?.let { element -> showHandlers.forEach { it.invoke(element) } }
    }

    override fun updateFilter(tree: JTree, treeNode: BrowserTreeNode, expand: Boolean) {
        super.updateFilter(tree, treeNode, expand)
        runShowHandlers(TreePath(treeNode.path))
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

    protected fun addMultiLineHtmlDetail(key: String, textResolver: (element: T) -> String): HtmlKeyValue<T> {
        val keyValue = HtmlKeyValue<T>(key, highlightTextArea())
        addKeyValue(keyValue)
        showHandlers.add { element ->
            keyValue.valueLabel.text = textResolver(element)
            keyValue.show(element)
        }
        return keyValue
    }

    protected fun addMultiLinePlainDetail(key: String, textResolver: (element: T) -> String): MultiLineKeyValue<T> {
        val keyValue = MultiLineKeyValue<T>(key, multiLineLabel())
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
            add(classElementOpener, "newline unrel, spanx")
            showHandlers.add { element ->
                classElementOpener.setConstant(constantResolver(element))
            }
        }
    }

    private fun ExtendedJLabel.setupMouseListener(mouseListener: MouseListener) {
        labelToMouseListener[this]?.let { removeMouseListener(it) }
        addMouseListener(mouseListener)
        labelToMouseListener[this] = mouseListener
    }

    private fun ExtendedJLabel.applyComment(constantPoolIndex: Int) {
        toolTipText = text
        text = "<" + getConstantPoolEntryName(constantPoolIndex) + ">"
    }

    abstract class KeyValue<T : Any, out L>(key: String, val valueLabel: L, val commentLabel: L? = null) where L : JComponent, L : TextDisplay {

        val keyLabel = ExtendedJLabel(key)
        private var visibilityPredicate: ((T) -> Boolean)? = null

        fun visibilityPredicate(visibilityPredicate: (T) -> Boolean) {
            this.visibilityPredicate = visibilityPredicate
        }

        fun show(element: T) {
            visibilityPredicate?.let {
                val show = it(element)
                keyLabel.isVisible = show
                valueLabel.isVisible = show
                commentLabel?.isVisible = show
            }
        }
    }

    class DefaultKeyValue<T : Any>(key: String, valueLabel: ExtendedJLabel, commentLabel: ExtendedJLabel? = null) : KeyValue<T, ExtendedJLabel>(key, valueLabel, commentLabel)

    class MultiLineKeyValue<T : Any>(key: String, multiLineLabel: MultiLineLabel) : KeyValue<T, MultiLineLabel>(key, multiLineLabel, null)

    class HtmlKeyValue<T : Any>(key: String, valueLabel: HtmlDisplayTextArea, commentLabel: HtmlDisplayTextArea? = null) : KeyValue<T, HtmlDisplayTextArea>(key, valueLabel, commentLabel) {
        fun linkHandler(handler: (String) -> Unit) {
            valueLabel.addHyperlinkListener { e ->
                if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    val description = e.description
                    handler(description)
                }
            }
        }
    }
}
