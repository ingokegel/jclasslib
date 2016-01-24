/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.AbstractDetailPane
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.BrowserTreeNode
import org.gjt.jclasslib.browser.detail.elementvalues.ClassElementValueEntryDetailPane
import org.gjt.jclasslib.browser.detail.elementvalues.ConstElementValueEntryDetailPane
import org.gjt.jclasslib.browser.detail.elementvalues.EnumElementValueEntryDetailPane
import org.gjt.jclasslib.browser.detail.elementvalues.GenericElementValueDetailPane
import org.gjt.jclasslib.structures.elementvalues.ClassElementValue
import org.gjt.jclasslib.structures.elementvalues.ConstElementValue
import org.gjt.jclasslib.structures.elementvalues.ElementValue
import org.gjt.jclasslib.structures.elementvalues.EnumElementValue

import javax.swing.*
import javax.swing.border.Border
import javax.swing.tree.TreePath
import java.awt.*
import java.util.HashMap

class ElementValueDetailPane(services: BrowserServices) : AbstractDetailPane(services) {

    private val elementTypeToDetailPane = HashMap<Class<out ElementValue>, AbstractDetailPane>()

    private val specificInfoPane: JPanel = JPanel().apply {
        border = createTitledBorder("Specific info:")
        layout = CardLayout()
        add(JPanel(), NAME_UNKNOWN)
        addCard(ConstElementValue::class.java, ConstElementValueEntryDetailPane(services))
        addCard(ClassElementValue::class.java, ClassElementValueEntryDetailPane(services))
        addCard(EnumElementValue::class.java, EnumElementValueEntryDetailPane(services))
    }

    private val genericInfoPane: GenericElementValueDetailPane = GenericElementValueDetailPane(services).apply {
        border = createTitledBorder("Generic info:")
    }

    override fun setupComponent() {
        layout = BorderLayout()
        add(genericInfoPane.displayComponent, BorderLayout.NORTH)
        add(specificInfoPane, BorderLayout.CENTER)
    }

    override fun show(treePath: TreePath) {
        val elementValue = (treePath.lastPathComponent as BrowserTreeNode).element as ElementValue?
        if (elementValue == null) {
            showCard(NAME_UNKNOWN)
        } else {
            val detailPane = elementTypeToDetailPane[elementValue.javaClass]
            if (detailPane != null) {
                showCard(elementValue.javaClass.name)
                detailPane.show(treePath)
            }
        }
        genericInfoPane.show(treePath)
    }

    private fun showCard(cardName: String) {
        (specificInfoPane.layout as CardLayout).show(specificInfoPane, cardName)
    }

    private fun JPanel.addCard(elementValueClass: Class<out ElementValue>, detailPane: AbstractDetailPane) {
        add(detailPane.displayComponent, elementValueClass.name)
        elementTypeToDetailPane.put(elementValueClass, detailPane)
    }

    private fun createTitledBorder(title: String): Border {
        return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title)
    }

    companion object {
        private val NAME_UNKNOWN = "Unknown"
    }
}

