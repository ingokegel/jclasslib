/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import com.install4j.runtime.installer.frontend.GUIHelper
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.attributes.CodeAttributeDetailPane
import org.gjt.jclasslib.browser.detail.attributes.code.ByteCodeDocument.InstructionLink
import org.gjt.jclasslib.browser.detail.attributes.code.ByteCodeDocument.OffsetLink
import org.gjt.jclasslib.browser.detail.attributes.document.AttributeDocument
import org.gjt.jclasslib.browser.detail.attributes.document.DocumentDetailPane
import org.gjt.jclasslib.bytecode.*
import org.gjt.jclasslib.io.createInstruction
import org.gjt.jclasslib.io.writeByteCode
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.util.GUIHelper.getParentWindow
import java.awt.event.MouseEvent
import javax.swing.JMenu
import javax.swing.JOptionPane
import javax.swing.JPopupMenu
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener
import javax.swing.text.StyleContext

class ByteCodeDetailPane(services: BrowserServices, private val codeAttributeDetailPane: CodeAttributeDetailPane) : DocumentDetailPane<CodeAttribute, ByteCodeDocument>(CodeAttribute::class.java, ByteCodeDocument::class.java, services) {

    init {
        name = getString("code.tab.bytecode")
    }

    override fun createDocument(styles: StyleContext, attribute: CodeAttribute, classFile: ClassFile): ByteCodeDocument =
            ByteCodeDocument(styles, attribute, services.classFile)

    override fun offsetToPosition(offset: Int) = attributeDocument.getPosition(offset)

    override fun linkTriggered(link: AttributeDocument.Link, event: MouseEvent) {
        super.linkTriggered(link, event)
        when (link) {
            is OffsetLink -> {
                removeActiveHighlight()
                scrollToOffset(link.targetOffset)
                updateHistory(link.targetOffset)
            }
            is InstructionLink -> {
                lockHighlight()
                JPopupMenu().apply {
                    val instruction = link.instruction
                    add(getString("action.show.jvm.spec")).apply {
                        addActionListener {
                            services.showURL(instruction.opcode.docUrl)
                        }
                    }
                    add(getString("action.replace.opcode")).apply {
                        addActionListener {
                            replaceOpcode(instruction)
                        }
                    }
                    addImmediateEditActions(instruction)
                    addPopupMenuListener(object : PopupMenuListener {
                        override fun popupMenuWillBecomeVisible(e: PopupMenuEvent) {
                        }

                        override fun popupMenuWillBecomeInvisible(e: PopupMenuEvent) {
                            unlockHighlight()
                        }

                        override fun popupMenuCanceled(e: PopupMenuEvent) {
                        }
                    })
                }.show(textPane, event.x, event.y)
            }
        }
    }

    private fun JPopupMenu.addImmediateEditActions(instruction: Instruction) {
        val groupMenuItems = mutableMapOf<String, JMenu>()
        for (action in getImmediateEditActions(instruction)) {
            val actionGroup = action.group
            val actionName = action.name
            val menuItem = if (actionGroup == null) {
                add(actionName)
            } else {
                groupMenuItems.getOrPut(actionGroup) {
                    JMenu(actionGroup).also { add(it) }
                }.add(actionName)
            }
            menuItem.apply {
                addActionListener {
                    if (action.executeRaw(instruction, this@ByteCodeDetailPane.getParentWindow())) {
                        modifyInstructions()
                    }
                }
            }
        }
    }

    private fun replaceOpcode(instruction: Instruction) {
        val replacementOpcodes = getStackCompatibleReplacementOpcodes(instruction)
        if (replacementOpcodes.isEmpty()) {
            GUIHelper.showMessage(this, getString("no.compatible.opcode"), null, JOptionPane.WARNING_MESSAGE)
        } else {
            val opcode = instruction.opcode
            val newOpcode = JOptionPane.showInputDialog(
                    getParentWindow(),
                    getString("choose.compatible.opcode"),
                    getString("replace.opcode.title"),
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    replacementOpcodes.toTypedArray(),
                    opcode
            ) as Opcode?
            if (newOpcode != null && newOpcode != opcode) {
                val newInstruction = createInstruction(newOpcode, (instruction as? HasWide)?.isWide == true)
                newInstruction.copyFrom(instruction)
                val offset = instruction.offset
                modifyInstructions { instructions ->
                    val index = instructions.indexOfFirst { it.offset == offset }
                    check(index > -1)
                    check(instructions.getOrNull(index) == instruction)
                    instructions[index] = newInstruction
                }
            }
        }
    }

    private fun modifyInstructions(modifier: (MutableList<Instruction>) -> Unit = {}) {
        attributeDocument.lastInstructions?.let { instructions ->
            modifier(instructions)
            lastAttribute?.let {
                it.code = writeByteCode(instructions)
            }
            removeActiveHighlight()
            modified()
        }
    }

    override fun makeVisible() {
        super.makeVisible()
        codeAttributeDetailPane.selectByteCodeDetailPane()
    }
}

private fun Instruction.copyFrom(instruction: Instruction) {
    when (this) {
        is SimpleInstruction -> {}
        is SimpleImmediateByteInstruction -> immediateByte = (instruction as ImmediateByteInstruction).immediateByte
        is ImmediateShortInstruction -> immediateShort = (instruction as ImmediateShortInstruction).immediateShort
        is AbstractBranchInstruction -> branchOffset = (instruction as AbstractBranchInstruction).branchOffset
        else -> error("Not implemented")
    }
}
