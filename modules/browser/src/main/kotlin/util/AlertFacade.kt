/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import com.install4j.runtime.alert.Alert
import org.gjt.jclasslib.browser.BrowserBundle
import org.jetbrains.annotations.Nls
import java.awt.Component

enum class AlertType {
    INFORMATION, WARNING, ERROR, QUESTION
}

private val discardCancelOptions by lazy { arrayOf(BrowserBundle.getString("button.discard"), BrowserBundle.getString("button.cancel")) }
private val yesNoOptions by lazy { arrayOf(BrowserBundle.getString("button.yes"), BrowserBundle.getString("button.no")) }
private val okCancelOptions by lazy { arrayOf(BrowserBundle.getString("action.ok"), BrowserBundle.getString("button.cancel")) }

var alertFacade: AlertFacade = object : AlertFacade {
    override fun showOptionDialog(parent: Component?, mainMessage: String, contentMessage: String?, options: Array<String>, alertType: AlertType, suppressionShown: Boolean) =
        Alert.create<String>(parent, GUIHelper.MESSAGE_TITLE, mainMessage, contentMessage)
                .addButtons(options)
                .defaultButton(options[0])
                .cancelButton(options[options.size - 1])
                .alertType(alertType.convert())
                .suppressionShown(suppressionShown)
                .show()
                .let {
                    OptionAlertResult(it.selectedIndex, it.isSuppressionSelected)
                }

    override fun showMessage(parent: Component?, mainMessage: String, contentMessage: String?, alertType: AlertType, suppressionShown: Boolean) =
        Alert.create<Any>(parent, GUIHelper.MESSAGE_TITLE, mainMessage, contentMessage)
                .mainMessageRedundant(true)
                .alertType(alertType.convert())
                .suppressionShown(suppressionShown)
                .show()
                .isSuppressionSelected

    private fun AlertType.convert(): com.install4j.runtime.alert.AlertType =
        when (this) {
            AlertType.INFORMATION -> com.install4j.runtime.alert.AlertType.INFORMATION
            AlertType.WARNING -> com.install4j.runtime.alert.AlertType.WARNING
            AlertType.ERROR -> com.install4j.runtime.alert.AlertType.ERROR
            AlertType.QUESTION -> com.install4j.runtime.alert.AlertType.QUESTION
        }
}

interface AlertFacade {
    fun showOptionDialog(parent: Component?, @Nls mainMessage: String, @Nls contentMessage: String?, @Nls options: Array<String>, alertType: AlertType, suppressionShown: Boolean = false): OptionAlertResult
    fun showMessage(parent: Component?, @Nls mainMessage: String, @Nls contentMessage: String?, alertType: AlertType, suppressionShown: Boolean = false): Boolean

    fun showYesNoDialog(parent: Component?, @Nls mainMessage: String, @Nls contentMessage: String?, suppressionShown: Boolean = false): OptionAlertResult {
        return showOptionDialog(parent, mainMessage, contentMessage, yesNoOptions, AlertType.QUESTION, suppressionShown)
    }

    fun showOkCancelDialog(parent: Component?, @Nls mainMessage: String, @Nls contentMessage: String?, suppressionShown: Boolean = false): OptionAlertResult {
        return showOptionDialog(parent, mainMessage, contentMessage, okCancelOptions, AlertType.QUESTION, suppressionShown)
    }

    fun showDiscardCancelDialog(parent: Component?, @Nls mainMessage: String, @Nls contentMessage: String?, suppressionShown: Boolean = false): OptionAlertResult {
        return showOptionDialog(parent, mainMessage, contentMessage, discardCancelOptions, AlertType.QUESTION, suppressionShown)
    }

    fun showMessage(parent: Component?, throwable: Throwable) {
        showMessage(parent, BrowserBundle.getString("message.error.occurred"), throwable.message, AlertType.ERROR)
    }

    fun showMessage(parent: Component?, @Nls mainMessage: String, alertType: AlertType) {
        showMessage(parent, mainMessage, null, alertType)
    }
}

data class OptionAlertResult(val selectedIndex: Int, val suppressionSelected: Boolean)