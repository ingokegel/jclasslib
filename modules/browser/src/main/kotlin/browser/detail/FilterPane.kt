/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import com.formdev.flatlaf.FlatClientProperties
import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.structures.Structure
import org.gjt.jclasslib.util.EnumButtonGroup
import org.gjt.jclasslib.util.MatchType
import org.gjt.jclasslib.util.TitledSeparator
import org.jetbrains.annotations.Nls
import java.util.regex.Matcher
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

abstract class FilterPane<out T, in S : Structure>(private val detailPane: DetailPane<*>) : JPanel() {

    private val filterCheckboxes = getAllFilterKeys().associateWith {
        JCheckBox(it.toString()).apply {
        addActionListener {
            updateFilter()
        }
    }
    }

    protected abstract fun getAllFilterKeys(): Collection<T>
    protected abstract fun isElementTextFiltered(element: S, filterText: String, matchType: MatchType, matcher: Matcher?): Boolean
    protected abstract fun getFilterKeys(element: S): Collection<T>

    private val buttonGroup = EnumButtonGroup(FilterMode.entries) { selectedValue ->
        filterMode = selectedValue
    }

    private val matchTypeDropDown = JComboBox(MatchType.entries.toTypedArray()).apply {
        selectedItem = MatchType.CONTAINS
        addActionListener {
            updateFilter()
        }
    }

    private val filterTextField = JTextField().apply {
        putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, getString("enter.filter.expression"))
        putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true)
        putClientProperty(FlatClientProperties.TEXT_FIELD_CLEAR_CALLBACK, Runnable {
            text = ""
            updateFilter()
        })

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
                if (text.isNotEmpty()) {
                    textFilterEntered()
                }
                updateFilter()
            }
        })
    }

    private val filterComponents = mutableSetOf<JComponent>()

    init {
        layout = MigLayout("insets 0, wrap", "[grow]")

        add(TitledSeparator(getString("filter.title")), "growx")
        add(buttonGroup.getButton(FilterMode.ALL))
        add(buttonGroup.getButton(FilterMode.SELECTED), "wrap unrel")
        addComponents()
    }

    val isShowAll: Boolean
        get() = filterMode == FilterMode.ALL

    var filterMode: FilterMode
        get() = buttonGroup.selectedValue
        set(filterMode) {
            buttonGroup.selectedValue = filterMode
            val enabled = filterMode == FilterMode.SELECTED
            filterComponents.forEach { it.isEnabled = enabled }
            updateFilter()

        }

    protected open fun addComponents() {
        add(filterComponent(JLabel(getString("text.filter.label"))), "split, $RADIO_BUTTON_INSET")
        add(filterComponent(matchTypeDropDown))
        add(filterComponent(filterTextField), "wrap unrel")
        filterCheckboxes.values.forEachIndexed { i, checkBox ->
            add(filterComponent(checkBox), if (i % 2 == 0) "split, sgx col1, gapright para, $RADIO_BUTTON_INSET" else "sgx col2, wrap")
        }
        add(filterComponent(JButton(getString("action.filter.toggle.all")).apply {
            addActionListener {
                toggleCheckboxes(!filterCheckboxes.values.all { it.isSelected })
            }
        }), "newline unrel, $RADIO_BUTTON_INSET")
    }

    protected fun <T : JComponent> filterComponent(component: T): T {
        component.isEnabled = false
        filterComponents.add(component)
        return component
    }

    fun updateFilterCheckboxes(elements: Collection<S>) {
        val filterText = filterTextField.text.trim()
        val matchType = matchTypeDropDown.selectedItem as MatchType
        val matcher = matchType.createMatcher(filterText)
        val statistics = elements.filter { isElementTextFiltered(it, filterText, matchType, matcher) }
                .flatMap { getFilterKeys(it) }
                .groupingBy { it }
                .eachCount()
        filterCheckboxes.keys.forEach {filterKey ->
            filterCheckboxes[filterKey]?.apply {
                text = "${filterKey.toString()} (${statistics[filterKey] ?: 0})"
            }
        }
    }

    fun isElementShown(element: S): Boolean {
        val filterText = filterTextField.text.trim()
        val matchType = matchTypeDropDown.selectedItem as MatchType
        val matcher = matchType.createMatcher(filterText)
        return isShowAll || (
                filterCheckboxes.any { it.key in getFilterKeys(element) && it.value.isSelected } &&
                        isElementTextFiltered(element, filterText, matchType, matcher)
                )
    }

    private fun updateFilter() {
        detailPane.updateFilter()
    }

    private fun textFilterEntered() {
        if (filterCheckboxes.values.none { it.isSelected }) {
            toggleCheckboxes(true)
        }
    }

    private fun toggleCheckboxes(selected: Boolean) {
        filterCheckboxes.values.forEach { it.isSelected = selected }
        updateFilter()
    }

    enum class FilterMode(@param:Nls val verbose: String) {
        ALL(getString("filter.mode.all")),
        SELECTED(getString("filter.mode.selected"));

        override fun toString() = verbose
    }

    companion object {
        val RADIO_BUTTON_INSET = "gapleft ${JRadioButton().preferredSize.width}"
    }

}
