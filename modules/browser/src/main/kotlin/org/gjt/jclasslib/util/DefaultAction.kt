/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import org.gjt.jclasslib.browser.BrowserMDIFrame
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.KeyStroke

class DefaultAction(name: String, shortDescription: String? = null, smallIconFileName: String? = null, largeIconFileName: String? = null, private val action: () -> Unit) : AbstractAction(name) {
    init {
        val smallIcon = if (smallIconFileName != null) BrowserMDIFrame.getIcon(smallIconFileName) else GUIHelper.ICON_EMPTY
        putValue(SMALL_ICON, smallIcon)
        if (largeIconFileName != null) {
            putValue(LARGE_ICON_KEY, BrowserMDIFrame.getIcon(largeIconFileName))
        }
        if (shortDescription != null) {
            putValue(SHORT_DESCRIPTION, shortDescription)
        }
    }

    override fun actionPerformed(ev: ActionEvent) = invoke()

    operator fun invoke() {
        action.invoke()
    }

    fun accelerator(keyCode: Int, modifiers: Int = MENU_MODIFIER) {
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyCode, modifiers))
    }

    fun disabled() {
        isEnabled = false
    }

    fun createImageButton() = JButton(this).apply {
        text = null
        minimumSize = IMAGE_BUTTON_SIZE
        preferredSize = IMAGE_BUTTON_SIZE
        maximumSize = IMAGE_BUTTON_SIZE
    }

    fun createTextButton() = JButton(this).apply {
        icon = null
    }

    fun applyAcceleratorTo(component: JComponent) {
        val key = Object()
        component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(getValue(ACCELERATOR_KEY) as KeyStroke, key)
        component.actionMap.put(key, this)
    }

    companion object {
        val MENU_MODIFIER = Toolkit.getDefaultToolkit().menuShortcutKeyMask
        private val IMAGE_BUTTON_SIZE = Dimension(30, 30)
    }


}

