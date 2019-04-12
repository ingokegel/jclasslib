/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import javax.swing.JComponent
import javax.swing.JTree
import javax.swing.TransferHandler

class BrowserNodeTransferHandler(private val services: BrowserServices) : TransferHandler() {

    override fun getSourceActions(c: JComponent): Int = COPY

    override fun createTransferable(c: JComponent): Transferable? {
        if (c !is JTree || c.selectionPath == null) {
            return null
        }

        val text = services.browserComponent.detailPane.currentDetailPane.clipboardText
        return if (text != null) {
            StringSelection(text)
        } else {
            null
        }
    }
}
