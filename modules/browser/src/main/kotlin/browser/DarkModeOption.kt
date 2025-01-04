/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import com.install4j.api.UiUtil
import org.gjt.jclasslib.browser.BrowserBundle.getString

enum class DarkModeOption {
    AUTO {
        override val displayName get() = getString("menu.dark.mode.auto")
        override fun isDarkMode() = UiUtil.isDarkDesktop()
    },
    ON {
        override val displayName get() = getString("menu.dark.mode.on")
        override fun isDarkMode() = true
    },
    OFF {
        override val displayName get() = getString("menu.dark.mode.off")
        override fun isDarkMode() = false
    };

    // Cannot be a constructor parameter because the class is used in Main.kt
    abstract val displayName: String
    abstract fun isDarkMode(): Boolean
}