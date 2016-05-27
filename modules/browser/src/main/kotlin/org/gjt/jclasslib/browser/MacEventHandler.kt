/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import com.apple.eawt.Application

object MacEventHandler {
    fun init() {
        Application.getApplication().apply {
            setAboutHandler { getActiveBrowserFrame()?.aboutAction?.invoke()}
        }
    }
}
