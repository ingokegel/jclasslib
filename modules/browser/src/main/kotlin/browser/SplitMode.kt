/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.jetbrains.annotations.Nls
import java.awt.event.KeyEvent

enum class SplitMode(@Nls val actionName: String, val actionDescription: String, val accelerator: Int) {
    NONE(getString("action.merge.splits"), getString("action.merge.splits.description"), KeyEvent.VK_M),
    HORIZONTAL(getString("action.split.horizontally"), getString("action.split.horizontally.description"), KeyEvent.VK_H),
    VERTICAL(getString("action.split.vertically"), getString("action.split.vertically.description"), KeyEvent.VK_V),
    BOTH(getString("action.split.4way"), getString("action.split.4way.description"), KeyEvent.VK_B);

    companion object {
        fun getByName(name : String?) = values().firstOrNull { it.name == name } ?: NONE
    }
}