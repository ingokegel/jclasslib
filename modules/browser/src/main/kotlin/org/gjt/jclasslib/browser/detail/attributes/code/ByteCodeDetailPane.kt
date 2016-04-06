/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.bytecode.Instruction
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.util.DefaultAction
import org.gjt.jclasslib.util.GUIHelper
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.*
import javax.swing.tree.TreePath

class ByteCodeDetailPane(services: BrowserServices) : DetailPane<CodeAttribute>(CodeAttribute::class.java, services) {

    private val instructionToURL = HashMap<String, String>()

    private val showDescriptionAction = DefaultAction("Show Description", "Show detailed information about the selected instruction.") {
        val opcode = instructionsDropDown.selectedItem
        if (opcode != null) {
            val url = instructionToURL[opcode]
            if (url != null) {
                services.showURL(url)
            }
        }
    }

    private val copyAction = DefaultAction("Copy to clipboard", "Copy the entire byte code to the clipboard.") {
        byteCodeDisplay.copyToClipboard()
    }

    private val byteCodeDisplay: ByteCodeDisplay = ByteCodeDisplay(this)

    val scrollPane: JScrollPane = JScrollPane(byteCodeDisplay).apply {
        viewport.background = Color.WHITE
        object : MouseAdapter() {
            override fun mousePressed(event: MouseEvent?) {
                requestFocus()
            }
        }.let {
            horizontalScrollBar.addMouseListener(it)
            verticalScrollBar.addMouseListener(it)
        }
        addMouseWheelListener { requestFocus() }
    }

    private val instructionsDropDown = JComboBox<String>()

    init {
        name = "Bytecode"
    }

    override fun setupComponent() {
        layout = BorderLayout()
        add(Box.createHorizontalBox().apply {
            add(JPanel(FlowLayout(FlowLayout.LEFT, 6, 0)).apply {
                add(JLabel("Used instructions:"))
                add(instructionsDropDown, BorderLayout.CENTER)
                add(showDescriptionAction.createTextButton())
            })
            add(Box.createHorizontalGlue())
            add(copyAction.createTextButton())
        }, BorderLayout.SOUTH)
        add(scrollPane, BorderLayout.CENTER)
    }

    fun setCurrentInstructions(instructions: List<Instruction>) {
        instructionToURL.clear()
        val mnemonics = TreeSet<String>()
        instructions.forEach { instruction ->
            val verbose = instruction.opcode.verbose
            if (mnemonics.add(verbose)) {
                instructionToURL.put(verbose, instruction.opcode.docUrl)
            }
        }
        instructionsDropDown.model = DefaultComboBoxModel(mnemonics.toTypedArray())
    }

    override fun show(treePath: TreePath) {
        val attribute = getElement(treePath)
        if (byteCodeDisplay.codeAttribute != attribute) {
            withWaitCursor {
                byteCodeDisplay.setCodeAttribute(attribute, services.classFile)
                scrollPane.setRowHeaderView(CounterDisplay(byteCodeDisplay))
                byteCodeDisplay.scrollRectToVisible(GUIHelper.RECT_ORIGIN)
                scrollPane.validate()
                scrollPane.repaint()
            }
        }
    }

    private fun withWaitCursor(function: () -> Unit) {
        val browserComponent = services.browserComponent
        browserComponent.cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
        try {
            function.invoke()
        } finally {
            browserComponent.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
        }
    }

    fun scrollToOffset(offset: Int) {
        byteCodeDisplay.scrollToOffset(offset)
    }

    override val clipboardText: String?
        get() = byteCodeDisplay.clipboardText

}

