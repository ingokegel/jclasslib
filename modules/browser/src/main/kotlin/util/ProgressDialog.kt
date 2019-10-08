/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util

import net.miginfocom.swing.MigLayout
import java.awt.Window
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.concurrent.ExecutionException
import javax.swing.*

class ProgressDialog(parent: Window, message: String, var task: () -> Unit = {}) : JDialog(parent) {

    private val progressBar: JProgressBar = JProgressBar().apply {
        preferredSize = preferredSize.apply {
            width = 200
        }
    }

    private val messageLabel: JLabel = JLabel(message)

    init {
        setupComponent()
        setupEventHandlers()
    }

    override fun setVisible(visible: Boolean) {
        progressBar.isIndeterminate = visible
        if (visible) {
            GUIHelper.centerOnParentWindow(this, owner)
        }
        super.setVisible(visible)
    }

    private fun setupComponent() {
        (contentPane as JPanel).apply {
            layout = MigLayout("wrap", "[grow]")

            add(messageLabel)
            add(progressBar, "growx")

            title = GUIHelper.MESSAGE_TITLE
            isModal = true
            pack()
        }
    }

    private fun setupEventHandlers() {
        addComponentListener(object : ComponentAdapter() {
            override fun componentShown(event: ComponentEvent?) {
                object : SwingWorker<Unit, Unit>() {
                    override fun doInBackground() {
                        task()
                    }

                    override fun done() {
                        isVisible = false
                        try {
                            get()
                        } catch(e: ExecutionException) {
                            e.cause?.printStackTrace()
                            GUIHelper.showMessage(parent, "An error occurred: ${e.cause?.javaClass}: ${e.cause?.message}", JOptionPane.ERROR_MESSAGE)
                        }
                    }
                }.execute()
            }
        })
    }
}
