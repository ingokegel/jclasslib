/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import com.install4j.api.UiUtil
import org.gjt.jclasslib.browser.BrowserBundle.getString

enum class DarkModeOption(val displayName: String) {
    AUTO(getString("menu.dark.mode.auto")) {
        override fun isDarkMode() = UiUtil.isDarkDesktop()
    },
    ON(getString("menu.dark.mode.on")) {
        override fun isDarkMode() = true
    },
    OFF(getString("menu.dark.mode.off")) {
        override fun isDarkMode() = false
    };

    abstract fun isDarkMode(): Boolean
}