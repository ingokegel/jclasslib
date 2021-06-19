/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util

import com.install4j.api.Util
import com.install4j.runtime.alert.Alert
import com.install4j.runtime.alert.AlertType
import com.install4j.runtime.filechooser.AbstractFileSystemChooser
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.jetbrains.annotations.Nls
import java.awt.Component
import java.awt.Window
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JScrollPane

object GUIHelper {

    const val MESSAGE_TITLE = "jclasslib"
    val YES_NO_OPTIONS by lazy { arrayOf(getString("button.yes"), getString("button.no")) }
    val ICON_EMPTY: Icon = EmptyIcon(16, 16)

    fun isMacOs() = System.getProperty("os.name").lowercase().startsWith("mac")

    fun showOptionDialog(parent: Component, @Nls mainMessage: String, @Nls contentMessage: String?, @Nls options: Array<String>, alertType: AlertType): Int {
        val alert = Alert.create<String>(parent, MESSAGE_TITLE, mainMessage, contentMessage)
                .addButtons(options)
                .defaultButton(options[0])
                .cancelButton(options[options.size - 1])
                .alertType(alertType)

        val alertResult = alert.show()
        return alertResult.selectedIndex
    }

    fun showMessage(parent: Component?, throwable: Throwable) {
        showMessage(parent, getString("message.error.occurred"), throwable.message, AlertType.ERROR)
    }

    fun showMessage(parent: Component?, @Nls mainMessage: String, alertType: AlertType) {
        showMessage(parent, mainMessage, null, alertType)
    }

    fun showMessage(parent: Component?, @Nls mainMessage: String, @Nls contentMessage: String?, alertType: AlertType) {
        Alert.create<Any>(parent, MESSAGE_TITLE, mainMessage, contentMessage)
            .mainMessageRedundant(true)
            .alertType(alertType)
            .show()
    }

    fun centerOnParentWindow(window: Window, parentWindow: Window) {
        val x = parentWindow.x + (parentWindow.width - window.width) / 2
        val y = parentWindow.y + (parentWindow.height - window.height) / 2
        window.setLocation(x, y)
    }

    fun setDefaultScrollBarUnits(scrollPane: JScrollPane) {
        val unit = JLabel().font.size * 2
        scrollPane.apply {
            horizontalScrollBar.unitIncrement = unit
            verticalScrollBar.unitIncrement = unit
        }
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

}
