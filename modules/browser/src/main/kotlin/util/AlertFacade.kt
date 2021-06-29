/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import com.install4j.runtime.alert.Alert
import org.jetbrains.annotations.Nls
import java.awt.Component

enum class AlertType {
    INFORMATION, WARNING, ERROR, QUESTION
}

var alertFacade: AlertFacade = object : AlertFacade {
    override fun showOptionDialog(parent: Component?, mainMessage: String, contentMessage: String?, options: Array<String>, alertType: AlertType): Int {
        val alert = Alert.create<String>(parent, GUIHelper.MESSAGE_TITLE, mainMessage, contentMessage)
                .addButtons(options)
                .defaultButton(options[0])
                .cancelButton(options[options.size - 1])
                .alertType(alertType.convert())

        val alertResult = alert.show()
        return alertResult.selectedIndex
    }

    override fun showMessage(parent: Component?, mainMessage: String, contentMessage: String?, alertType: AlertType) {
        Alert.create<Any>(parent, GUIHelper.MESSAGE_TITLE, mainMessage, contentMessage)
                .mainMessageRedundant(true)
                .alertType(alertType.convert())
                .show()
    }

    private fun AlertType.convert(): com.install4j.runtime.alert.AlertType =
        when (this) {
            AlertType.INFORMATION -> com.install4j.runtime.alert.AlertType.INFORMATION
            AlertType.WARNING -> com.install4j.runtime.alert.AlertType.WARNING
            AlertType.ERROR -> com.install4j.runtime.alert.AlertType.ERROR
            AlertType.QUESTION -> com.install4j.runtime.alert.AlertType.QUESTION
        }
}

interface AlertFacade {
    fun showOptionDialog(parent: Component?, @Nls mainMessage: String, @Nls contentMessage: String?, @Nls options: Array<String>, alertType: AlertType): Int
    fun showMessage(parent: Component?, @Nls mainMessage: String, @Nls contentMessage: String?, alertType: AlertType)
}