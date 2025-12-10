/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import org.gjt.jclasslib.browser.BrowserBundle
import org.jetbrains.annotations.Nls
import java.awt.Window
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JComponent
import javax.swing.JDialog

abstract class StandardDialog(parentWindow: Window?, @Nls title: String): JDialog(parentWindow, title) {
    protected val okAction = DefaultAction(okButtonText) {
        doOk()
    }

    protected open val okButtonText: String
        get() = BrowserBundle.getString("action.ok")

    var isCanceled: Boolean = false
        private set

    protected val cancelAction = DefaultAction(BrowserBundle.getString("action.cancel")) {
        doCancel()
    }.apply {
        accelerator(KeyEvent.VK_ESCAPE, 0)
        applyAcceleratorTo(contentPane as JComponent)
    }

    protected open fun doOk() {
        isVisible = false
    }

    protected open fun doCancel() {
        isVisible = false
        isCanceled = true
    }

    override fun setVisible(visible: Boolean) {
        if (visible) {
            centerOnParentWindow(this, owner)
            isCanceled = false
        }
        super.setVisible(visible)
    }

    protected fun setupComponent() {
        pack() // Required for FlatLaf with JetBrains JRE custom decorations enabled, otherwise bounds on HiDPI screens are wrong
        (contentPane as JComponent).apply {
            addContent(this)

            add(okAction.createTextButton().apply {
                this@StandardDialog.getRootPane().defaultButton = this
            }, "span, split, tag ok")
            add(cancelAction.createTextButton(), "tag cancel")

            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(event: WindowEvent?) {
                    cancelAction()
                }

                override fun windowActivated(e: WindowEvent?) {
                    conditionalUpdate()
                }

                override fun windowOpened(e: WindowEvent?) {
                    windowOpened()
                }
            })
        }
        if (isPack()) {
            pack()
        }
        isModal = true
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
    }

    protected open fun isPack(): Boolean {
        return false
    }

    protected abstract fun addContent(component: JComponent)

    protected open fun conditionalUpdate() {
    }

    protected open fun windowOpened() {
    }
}

abstract class SelectionDialog<T>(parentWindow: Window?, @Nls title: String) : StandardDialog(parentWindow, title) {
    fun select(): T? {
        isVisible = true
        dispose()
        return if (!isCanceled) selectedItem else null
    }

    protected abstract val selectedItem: T?
}