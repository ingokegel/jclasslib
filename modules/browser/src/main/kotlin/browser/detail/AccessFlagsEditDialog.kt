/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.structures.AccessFlag
import org.gjt.jclasslib.util.StandardDialog
import java.awt.Window
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel

class AccessFlagsEditDialog(selectedValue: Int, validAccessFlags: Set<AccessFlag>, parentWindow: Window?, delegateName: String?) : StandardDialog(
        parentWindow,
        getString("action.edit") + if (delegateName == null) "" else " [$delegateName]"
) {

    private val checkBoxes: Map<AccessFlag, JCheckBox> = mutableMapOf<AccessFlag, JCheckBox>().apply {
        for (accessFlag in validAccessFlags) {
            @Suppress("HardCodedStringLiteral")
            val extraText = " (0x" + "%04x".format(accessFlag.flag) + (accessFlag.sinceJava?.let { ", " + getString("since.0", it) } ?: "") + ")"
            put(accessFlag, JCheckBox(accessFlag.getAccessFlagText() + extraText))
        }
    }

    private fun AccessFlag.getAccessFlagText(): String = when (this) {
        AccessFlag.SUPER -> "super (historical)"
        else -> verbose
    }

    fun getAccessFlags(): Int = AccessFlag.composeFrom(checkBoxes.filter { it.value.isSelected }.keys)

    init {
        setupComponent()
        val selectedAccessFlags = AccessFlag.decompose(selectedValue, validAccessFlags)
        for (accessFlag in validAccessFlags) {
            requireNotNull(checkBoxes[accessFlag]).isSelected = accessFlag in selectedAccessFlags
        }
    }

    override fun addContent(jComponent: JComponent) {
        layout = MigLayout("wrap", "[grow]")
        add(JLabel(getString("valid.access.flags")), "wrap unrel")
        for (checkBox in checkBoxes.values) {
            add(checkBox)
        }
    }

    override fun isPack() = true
}