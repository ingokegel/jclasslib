/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import org.gjt.jclasslib.browser.BrowserFrame
import org.jetbrains.annotations.Nls
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.ActionEvent
import javax.swing.*

class DefaultAction(
        @Nls name: String,
        @Nls shortDescription: String? = null,
        iconFileName: String? = null,
        private val action: (action: DefaultAction) -> Unit
) : AbstractAction(name) {

    var lastButton: JComponent? = null
        private set

    init {
        if (iconFileName != null) {
            if (iconFileName.isEmpty()) {
                putValue(SMALL_ICON, GUIHelper.ICON_EMPTY)
            } else {
                putValue(SMALL_ICON, BrowserFrame.getSvgIcon(iconFileName, SMALL_ICON_SIZE))
                putValue(LARGE_ICON_KEY, BrowserFrame.getSvgIcon(iconFileName, LARGE_ICON_SIZE))
            }
        }
        if (shortDescription != null) {
            putValue(SHORT_DESCRIPTION, shortDescription)
        }
    }

    override fun actionPerformed(ev: ActionEvent) = invoke()

    operator fun invoke() {
        action.invoke(this)
    }

    fun accelerator(keyCode: Int, modifiers: Int = MENU_MODIFIER) {
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyCode, modifiers))
    }

    fun disabled() {
        isEnabled = false
    }

    fun createImageButton() = createButton().apply {
        text = null
        icon = getValue(SMALL_ICON) as Icon?
        fixedSize(IMAGE_BUTTON_SIZE)
        if (GUIHelper.isMacOs()) {
            putClientProperty("JButton.buttonType", "toolbar")
        }
    }

    fun createToolBarButton() = createButton().apply {
        text = null
        fixedSize(TOOL_BAR_BUTTON_SIZE)
        isFocusable = false
        if (GUIHelper.isMacOs()) {
            putClientProperty("JButton.buttonType", "toolbar")
        }
    }

    private fun JButton.fixedSize(size: Dimension) {
        minimumSize = size
        preferredSize = size
        maximumSize = size
    }

    fun createTextButton() = createButton().apply {
        icon = null
    }

    private fun createButton() = JButton(this).also {
        lastButton = it
    }

    fun applyAcceleratorTo(component: JComponent) {
        val key = Object()
        component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(getValue(ACCELERATOR_KEY) as KeyStroke, key)
        component.actionMap.put(key, this)
    }

    companion object {
        val MENU_MODIFIER = Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx
        private val IMAGE_BUTTON_SIZE = Dimension(26, 26)
        private val TOOL_BAR_BUTTON_SIZE = Dimension(35, 35)
        val SMALL_ICON_SIZE = Dimension(16, 16)
        val LARGE_ICON_SIZE = Dimension(24, 24)
    }

}

