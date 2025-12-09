/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.util

import com.install4j.api.Util
import javax.swing.Action
import javax.swing.JMenu
import javax.swing.JMenuItem

class MenuBarMenu(text: String) : JMenu(text) {
    override fun add(action: Action): JMenuItem {
        val menuItem = super.add(action)
        if (Util.isMacOS()) {
            menuItem.icon = null
        }
        return menuItem
    }

    override fun add(menuItem: JMenuItem): JMenuItem {
        if (Util.isMacOS()) {
            menuItem.icon = null
        }
        return super.add(menuItem)
    }
}
