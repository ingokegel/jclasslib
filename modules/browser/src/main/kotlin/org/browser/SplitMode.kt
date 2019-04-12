/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import java.awt.event.KeyEvent

enum class SplitMode(val actionName: String, val actionDescription: String, val accelerator: Int) {
    NONE("Merge all splits", "Merge all splits into a single tabbed pane", KeyEvent.VK_M),
    HORIZONTAL("Split horizontally", "Split the window area into two separate tabbed panes in a horizontal arrangement", KeyEvent.VK_H),
    VERTICAL("Split vertically", "Split the window area into two separate tabbed panes in a vertical arrangement", KeyEvent.VK_V),
    BOTH("4-way split", "Split the window area into 4 parts each with a tabbed pane", KeyEvent.VK_B);

    companion object {
        fun getByName(name : String?) = values().firstOrNull { it.name == name } ?: NONE
    }
}