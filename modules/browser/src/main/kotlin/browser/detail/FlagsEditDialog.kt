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

abstract class FlagsEditDialog<A>(selectedValue: Int, validFlags: Set<A>, parentWindow: Window?, delegateName: String?) : StandardDialog(
        parentWindow,
        getString("action.edit") + if (delegateName == null) "" else " [$delegateName]"
) {

    private val checkBoxes: Map<A, JCheckBox> = mutableMapOf<A, JCheckBox>().apply {
        for (flag in validFlags) {
            put(flag, JCheckBox(getFlagText(flag)))
        }
    }

    abstract fun getFlagText(flag: A): String
    abstract fun composeFrom(keys: Set<A>): Int
    abstract fun decompose(selectedValue: Int, validFlags: Set<A>): List<A>

    fun getFlags(): Int = composeFrom(checkBoxes.filter { it.value.isSelected }.keys)

    init {
        setupComponent()
        val selectedFlags = decompose(selectedValue, validFlags)
        for (flag in validFlags) {
            requireNotNull(checkBoxes[flag]).isSelected = flag in selectedFlags
        }
    }

    override fun addContent(jComponent: JComponent) {
        layout = MigLayout("wrap", "[grow]")
        add(JLabel(getString("valid.flags")), "wrap unrel")
        for (checkBox in checkBoxes.values) {
            add(checkBox)
        }
    }

    override fun isPack() = true
}

class AccessFlagsEditDialog(selectedValue: Int, validFlags: Set<AccessFlag>, parentWindow: Window?, delegateName: String?)
    : FlagsEditDialog<AccessFlag>(selectedValue, validFlags, parentWindow, delegateName
) {
    override fun getFlagText(flag: AccessFlag): String {
        @Suppress("HardCodedStringLiteral")
        val extraText = " (0x" + "%04x".format(flag.flag) + (flag.sinceJava?.let { ", " + getString("since.0", it) }
                ?: "") + ")"
        return flag.getAccessFlagText() + extraText
    }

    private fun AccessFlag.getAccessFlagText(): String = when (this) {
        AccessFlag.SUPER -> "super (historical)"
        else -> verbose
    }

    override fun composeFrom(keys: Set<AccessFlag>): Int = AccessFlag.composeFrom(keys)
    override fun decompose(selectedValue: Int, validFlags: Set<AccessFlag>): List<AccessFlag> {
        return AccessFlag.decompose(selectedValue, validFlags)
    }
}
