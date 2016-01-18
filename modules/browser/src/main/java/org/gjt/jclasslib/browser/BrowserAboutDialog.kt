/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import com.install4j.api.launcher.Variables
import org.gjt.jclasslib.util.DefaultAction
import org.gjt.jclasslib.util.GUIHelper
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.IOException
import java.util.*
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel

class BrowserAboutDialog(parent: JFrame) : JDialog(parent) {

    private val okAction = DefaultAction("OK") {
        isVisible = false
        dispose()
    }

    init {
        setupComponent()
    }

    private fun setupComponent() {
        (contentPane as JComponent).apply {
            border = GUIHelper.WINDOW_BORDER
            layout = GridBagLayout()

            val gc = GridBagConstraints().apply {
                insets = Insets(10, 50, 5, 50)
                gridx = 0
                gridy = GridBagConstraints.RELATIVE
                anchor = GridBagConstraints.CENTER
                weightx = 1.0
            }

            add(JLabel("jclasslib bytecode viewer").apply {
                font = font.deriveFont(Font.BOLD)
            }, gc)
            gc.insets.top = 0
            add(JLabel("Version " + getVersion()), gc)
            add(JLabel("Copyright ej-technologies GmbH, 2001-" + Calendar.getInstance().get(Calendar.YEAR)), gc)
            add(JLabel("Licensed under the General Public License"), gc)

            contentPane.add(okAction.createTextButton().apply {
                this@BrowserAboutDialog.getRootPane().defaultButton = this
            }, GridBagConstraints().apply {
                insets = Insets(20, 0, 0, 0)
                gridx = 0
                gridy = GridBagConstraints.RELATIVE
                anchor = GridBagConstraints.CENTER
                gc.fill = GridBagConstraints.NONE
            })
        }

        pack()
        isModal = true
        isResizable = false
        title = "About the jclasslib bytecode viewer"
        GUIHelper.centerOnParentWindow(this, owner)
        defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
    }

    private fun getVersion(): String {
        try {
            return Variables.getCompilerVariable("sys.version")
        } catch (e: IOException) {
            return "<unknown>"
        }
    }
}
