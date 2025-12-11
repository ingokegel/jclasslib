/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.usages

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info
import org.gjt.jclasslib.util.MatchType
import org.gjt.jclasslib.util.StandardDialog
import java.awt.Window
import java.util.*
import javax.swing.*

private val stringUsageDialogs = WeakHashMap<BrowserFrame, StringUsagesDialog>()

fun findString(browserFrame: BrowserFrame) {
    stringUsageDialogs.computeIfAbsent(browserFrame) { StringUsagesDialog(browserFrame) }.apply {
        isVisible = true
        if (!isCanceled) {
            findString(input, matchType, includeJdk, browserFrame)
        }
    }
}

private fun findString(spec: String, matchType: MatchType, includeJdk: Boolean, browserFrame: BrowserFrame) {
    val matcher = matchType.createMatcher(spec)
    val classUsages = findClassUsages(browserFrame, includeJdk, browserFrame) { constant ->
        if (constant is ConstantUtf8Info) {
            matchType.matches(constant.string, spec, matcher)
        } else {
            false
        }
    }
    if (classUsages.isNotEmpty()) {
        showClassUsages(classUsages, browserFrame, browserFrame)
    } else {
        showNoUsagesFoundMessage(browserFrame)
    }
}

private class StringUsagesDialog(parentWindow: Window?) : StandardDialog(parentWindow, getString("find.string.title")) {
    private val textField = JTextField()
    private val matchTypesDropDown = JComboBox(MatchType.entries.toTypedArray())
    private val includeJdkCheckBox = JCheckBox(getString("include.jdk"))

    init {
        setupComponent()
    }

    override fun addContent(component: JComponent) {
        with (component) {
            layout = MigLayout("wrap", "[][grow]")
            add(JLabel(getString("search.for.string")), "spanx")
            add(matchTypesDropDown)
            add(textField, "growx, wmin 300")
            add(includeJdkCheckBox, "skip")
        }
    }

    override fun isPack() = true

    override fun windowOpened() {
        super.windowOpened()
        textField.requestFocusInWindow()
    }

    val input: String get() = textField.text
    val matchType: MatchType get() = matchTypesDropDown.selectedItem as MatchType
    val includeJdk: Boolean get() = includeJdkCheckBox.isSelected
}

