/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.structures.Structure
import org.gjt.jclasslib.util.TitledSeparator
import java.awt.CardLayout
import java.util.*
import javax.swing.JPanel
import javax.swing.tree.TreePath

abstract class MultiDetailPane<T : Structure>(elementClass: Class<T>, services: BrowserServices) : DetailPane<T>(elementClass, services) {

    private val elementClassToDetailPane = HashMap<Class<out T>, DetailPane<*>>()
    private var currentDetailPane: DetailPane<*>? = null

    private val specificInfoPane: JPanel = JPanel().apply {
        layout = CardLayout()
        add(JPanel(), NAME_UNKNOWN)
    }

    private val genericInfoPane: DetailPane<*> = createGenericInfoPane()

    protected abstract fun addCards()
    protected abstract fun createGenericInfoPane(): DetailPane<*>

    override fun setupComponent() {
        addCards()

        layout = MigLayout("wrap", "[grow]", "[][shrinkprio 50]para[][grow]")
        add(TitledSeparator("Generic info"), "growx")
        add(genericInfoPane.displayComponent, "growx")
        add(TitledSeparator("Specific info"), "growx")
        add(specificInfoPane, "grow")
    }

    override fun show(treePath: TreePath) {
        @Suppress("UNCHECKED_CAST")
        val element = getElementOrNull(treePath)
        if (element == null) {
            showEmptyCard()
        } else {
            val detailPane = elementClassToDetailPane[element::class.java]
            if (detailPane != null) {
                currentDetailPane = detailPane
                showCard(element::class.java.name)
                detailPane.show(treePath)
            } else {
                showEmptyCard()
            }
        }
        genericInfoPane.show(treePath)
    }

    private fun showEmptyCard() {
        showCard(NAME_UNKNOWN)
        currentDetailPane = null
    }

    fun getDetailPane(elementValueClass: Class<out T>): DetailPane<*>? =
            elementClassToDetailPane[elementValueClass]

    override val clipboardText: String?
        get() = currentDetailPane?.clipboardText

    private fun showCard(cardName: String) {
        (specificInfoPane.layout as CardLayout).show(specificInfoPane, cardName)
    }

    protected fun addCard(elementValueClass: Class<out T>, detailPane: DetailPane<*>) {
        specificInfoPane.add(detailPane.displayComponent, elementValueClass.name)
        elementClassToDetailPane.put(elementValueClass, detailPane)
    }

    companion object {
        private val NAME_UNKNOWN = "Unknown"
    }
}

