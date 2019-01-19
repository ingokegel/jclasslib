/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.structures.ClassFile
import javax.swing.Action

interface BrowserServices {
    val classFile: ClassFile
    fun activate()
    val browserComponent: BrowserComponent
    val backwardAction: Action
    val forwardAction: Action
    fun openClassFile(className: String, browserPath: BrowserPath?)
    fun canOpenClassFiles(): Boolean
    fun showURL(urlSpec: String)
}

