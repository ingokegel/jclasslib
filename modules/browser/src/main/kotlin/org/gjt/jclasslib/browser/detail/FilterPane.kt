/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.util.EnumButtonGroup
import org.gjt.jclasslib.util.TitledSeparator
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

open class FilterPane(private val detailPane : DetailPane<*>) : JPanel() {

    val filterText: String
        get() = filterTextField.text.trim()

    private val buttonGroup = EnumButtonGroup(FilterMode.values()) { selectedValue ->
        filterMode = selectedValue
    }

    private val filterTextField = JTextField().apply {
        columns = 30
        document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(event: DocumentEvent) {
                modified()
            }

            override fun insertUpdate(event: DocumentEvent) {
                modified()
            }

            override fun removeUpdate(event: DocumentEvent) {
                modified()
            }

            private fun modified() {
                if (!text.isEmpty()) {
                    textFilterEntered()
                }
                updateFilter()
            }
        })
    }

    private val filterComponents = mutableSetOf<JComponent>()

    init {
        layout = MigLayout("wrap", "[grow]")

        add(TitledSeparator("Filter"), "growx")
        add(JSeparator(JSeparator.HORIZONTAL))
        add(buttonGroup.radioButtons[FilterMode.ALL])
        add(buttonGroup.radioButtons[FilterMode.SELECTED], "wrap unrel")
        addComponents()
    }

    val isShowAll: Boolean
        get() = filterMode == FilterMode.ALL

    var filterMode : FilterMode
        get() = buttonGroup.selectedValue
        set(filterMode) {
            buttonGroup.selectedValue = filterMode
            val enabled = filterMode == FilterMode.SELECTED
            filterComponents.forEach { it.isEnabled = enabled }
            updateFilter()

        }

    open protected fun addComponents() {
        add(filterComponent(JLabel("Text filter:")), "split, $RADIO_BUTTON_INSET")
        add(filterComponent(filterTextField))
        add(filterComponent(JButton("Clear").apply {
            addActionListener {
                filterTextField.text = ""
                updateFilter()
            }
        }), "wrap unrel")
    }

    open protected fun textFilterEntered() {

    }

    private fun updateFilter() {
        detailPane.updateFilter()
    }

    protected fun <T : JComponent> filterComponent(component : T) : T {
        component.isEnabled = false
        filterComponents.add(component)
        return component
    }

    enum class FilterMode(val verbose: String) {
        ALL("Show all"),
        SELECTED("Show selected");

        override fun toString() = verbose
    }

    companion object {
        val RADIO_BUTTON_INSET = "gapleft ${JRadioButton().preferredSize.width}"
    }

}
