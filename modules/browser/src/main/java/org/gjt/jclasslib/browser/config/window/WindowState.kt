/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.window

class WindowState {

    var fileName: String? = null
    var browserPath: BrowserPath? = null

    constructor(fileName: String, browserPath: BrowserPath) {
        this.fileName = fileName
        this.browserPath = browserPath
    }

    constructor(fileName: String) {
        this.fileName = fileName
    }

    constructor() {

    }

    override fun equals(other: Any?): Boolean {
        if (fileName == null || other == null || other !is WindowState) {
            return false
        }
        return fileName == other.fileName
    }

    override fun hashCode(): Int {
        return fileName?.hashCode() ?: 0
    }
}
