/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import org.jetbrains.annotations.Nls
import java.awt.event.ActionEvent
import javax.swing.DefaultButtonModel
import javax.swing.JButton
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener

class MenuButton(@Nls text: String, private val popupMenu: JPopupMenu) : JButton(text) {
    init {
        setModel(MenuButtonModel())
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        popupMenu.isEnabled = enabled
    }

    private inner class MenuButtonModel : DefaultButtonModel() {
        init {
            popupMenu.addPopupMenuListener(object : PopupMenuListener {
                override fun popupMenuWillBecomeInvisible(event: PopupMenuEvent) {
                    programmatic = true
                    if (isPressed) {
                        isPressed = false
                    }
                    if (isRollover) {
                        isRollover = cachedRollover
                    }
                    programmatic = false
                }

                override fun popupMenuWillBecomeVisible(event: PopupMenuEvent) {
                }

                override fun popupMenuCanceled(event: PopupMenuEvent) {}
            })
        }

        private var programmatic = false
        private var cachedRollover = false

        override fun setPressed(pressed: Boolean) {
            if (isPopupMenuShown()) {
                if (pressed) {
                    super.setPressed(true)
                    val bounds = this@MenuButton.bounds
                    val x = bounds.x
                    popupMenu.show(parent, x, bounds.y + bounds.height)
                } else {
                    if (programmatic || !popupMenu.isVisible) {
                        super.setPressed(false)
                    }
                }
            } else {
                super.setPressed(pressed)
            }
        }

        override fun setRollover(rollover: Boolean) {
            if (isPopupMenuShown()) {
                cachedRollover = rollover
                if (programmatic || rollover || !popupMenu.isVisible) {
                    super.setRollover(rollover)
                }
            } else {
                super.setRollover(rollover)
            }
        }

        override fun fireActionPerformed(e: ActionEvent) {
            if (popupMenu.componentCount == 1) {
                (popupMenu.getComponent(0) as JMenuItem).doClick()
            }
            super.fireActionPerformed(e)
        }

        private fun isPopupMenuShown() = popupMenu.componentCount > 1
    }
}
