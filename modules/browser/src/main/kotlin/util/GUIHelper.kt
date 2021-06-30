/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util

import com.install4j.api.Util
import com.install4j.runtime.filechooser.AbstractFileSystemChooser
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.jetbrains.annotations.Nls
import java.awt.Component
import java.awt.Window
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.SwingUtilities

object GUIHelper {

    const val MESSAGE_TITLE = "jclasslib"
    val YES_NO_OPTIONS by lazy { arrayOf(getString("button.yes"), getString("button.no")) }
    val DISCARD_CANCEL_OPTIONS by lazy { arrayOf(getString("button.discard"), getString("button.cancel")) }
    val ICON_EMPTY: Icon = EmptyIcon(16, 16)

    fun isMacOs() = System.getProperty("os.name").lowercase().startsWith("mac")

    fun showOptionDialog(parent: Component?, @Nls mainMessage: String, @Nls contentMessage: String?, @Nls options: Array<String>, alertType: AlertType): Int =
        alertFacade.showOptionDialog(parent, mainMessage, contentMessage, options, alertType)

    fun showMessage(parent: Component?, throwable: Throwable) {
        showMessage(parent, getString("message.error.occurred"), throwable.message, AlertType.ERROR)
    }

    fun showMessage(parent: Component?, @Nls mainMessage: String, alertType: AlertType) {
        showMessage(parent, mainMessage, null, alertType)
    }

    fun showMessage(parent: Component?, @Nls mainMessage: String, @Nls contentMessage: String?, alertType: AlertType) {
        alertFacade.showMessage(parent, mainMessage, contentMessage, alertType)
    }

    fun centerOnParentWindow(window: Window, parentWindow: Window) {
        val x = parentWindow.x + (parentWindow.width - window.width) / 2
        val y = parentWindow.y + (parentWindow.height - window.height) / 2
        window.setLocation(x, y)
    }

    fun showURL(urlSpec: String) {
        try {
            Util.showUrl(URL(urlSpec))
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    fun <T : AbstractFileSystemChooser<*>> T.applyPath(currentDirectory: String) : T {
        currentDirectory(currentDirectory.let { if (it.isNotEmpty()) File(it) else null })
        return this
    }

    fun JComponent.getParentWindow(): Window? = SwingUtilities.getWindowAncestor(this)

}
