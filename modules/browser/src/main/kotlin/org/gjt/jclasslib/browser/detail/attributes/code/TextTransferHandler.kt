/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.code

import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.text.DefaultEditorKit

class TextTransferHandler private constructor(private val byteCodeDisplay: ByteCodeDisplay) : TransferHandler() {

    init {
        byteCodeDisplay.apply {
            setInputMap(JComponent.WHEN_FOCUSED, createInputMap())
            actionMap = createActionMap()
            transferHandler = this@TextTransferHandler
            addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent?) {
                    byteCodeDisplay.requestFocus()
                }
            })
        }
    }

    override fun getSourceActions(c: JComponent): Int {
        return TransferHandler.COPY
    }

    override fun createTransferable(c: JComponent): Transferable? {
        val selectedText = byteCodeDisplay.selectedText
        if (selectedText == null) {
            return null
        } else {
            return StringSelection(selectedText)
        }
    }

    private fun createInputMap() = InputMap().apply {
        (UIManager.get("TextArea.focusInputMap") as InputMap?)?.let { standardInputMap ->
            standardInputMap.keys().filter { it != null && standardInputMap.get(it) == DefaultEditorKit.copyAction}.forEach { keyStroke ->
                put(keyStroke, DefaultEditorKit.copyAction)
            }
        }
    }

    private fun createActionMap() = ActionMap().apply {
        put(DefaultEditorKit.copyAction, TransferHandler.getCopyAction())
    }

    companion object {
        fun install(byteCodeDisplay: ByteCodeDisplay) {
            TextTransferHandler(byteCodeDisplay)
        }
    }
}