/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.structures.Structure
import org.gjt.jclasslib.util.TitledSeparator
import java.awt.CardLayout
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
        add(TitledSeparator(getString("detail.generic.info.title")), "growx")
        add(genericInfoPane.displayComponent, "growx")
        add(TitledSeparator(getString("detail.specific.info.title")), "growx")
        add(specificInfoPane, "grow")
    }

    override fun show(treePath: TreePath) {
        @Suppress("UNCHECKED_CAST")
        val element = getElementOrNull(treePath)
        if (element == null) {
            showEmptyCard()
        } else {
            val (detailPane, detailClass) = getDetailPaneWithClass(element::class.java)
            if (detailPane != null) {
                currentDetailPane = detailPane
                showCard(detailClass.name)
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
        getDetailPaneWithClass(elementValueClass).first

    private tailrec fun getDetailPaneWithClass(elementValueClass: Class<out T>): Pair<DetailPane<*>?, Class<out T>> {
        val detailPane = elementClassToDetailPane[elementValueClass]
        return if (detailPane != null) {
            detailPane to elementValueClass
        } else {
            val superclass = castToElementClass(elementValueClass.superclass)
            if (superclass == null) {
                null to elementValueClass
            } else {
                getDetailPaneWithClass(superclass)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun castToElementClass(c: Class<*>): Class<out T>? = if (elementClass.isAssignableFrom(c)) c as Class<out T> else null

    override val clipboardText: String?
        get() = currentDetailPane?.clipboardText

    private fun showCard(cardName: String) {
        (specificInfoPane.layout as CardLayout).show(specificInfoPane, cardName)
    }

    protected fun addCard(elementValueClass: Class<out T>, detailPane: DetailPane<*>) {
        specificInfoPane.add(detailPane.displayComponent, elementValueClass.name)
        elementClassToDetailPane[elementValueClass] = detailPane
    }

    companion object {
        private const val NAME_UNKNOWN = "Unknown"
    }
}

