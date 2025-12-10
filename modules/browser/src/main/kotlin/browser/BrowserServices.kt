/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.browser.config.classpath.FindResult
import org.gjt.jclasslib.structures.ClassFile
import javax.swing.Action

interface GlobalBrowserServices {
    fun openClassFile(className: String, browserPath: BrowserPath?)
    fun canOpenClassFiles(): Boolean
    fun canSaveClassFiles(): Boolean
    fun showURL(urlSpec: String)
    fun canScanClassFiles(): Boolean = false
    fun scanClassFiles(includeJdk: Boolean, classFileCallback: ClassFileCallback) {}
}

interface BrowserServices : GlobalBrowserServices {
    val classFile: ClassFile
    fun activate()
    val browserComponent: BrowserComponent
    val backwardAction: Action
    val forwardAction: Action
    fun modified()
}

fun interface ClassFileCallback {
    fun handleClassFile(classFile: ClassFile, findResult: FindResult)
}

fun ClassFileCallback.handleClassFile(findResult: FindResult, frame: BrowserFrame) {
    handleClassFile(readClassFile(findResult.fileName, frame), findResult)
}
