/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.detail.attributes.document.AttributeDocument
import org.gjt.jclasslib.browser.detail.attributes.document.LineNumberCounts
import org.gjt.jclasslib.bytecode.*
import org.gjt.jclasslib.io.ByteCodeReader
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.util.getValueColor
import util.LightOrDarkColor
import java.awt.Color
import java.util.*
import javax.swing.text.StyleContext

class ByteCodeDocument(styles: StyleContext, private val attribute: CodeAttribute, classFile: ClassFile) : AttributeDocument(styles, classFile) {

    private val offsetToPosition = HashMap<Int, Int>()
    private val invalidBranches = HashSet<Instruction>()
    private var offsetWidth: Int = 0

    init {
        setupDocument()
    }

    fun getPosition(offset: Int): Int = offsetToPosition[offset] ?: 0

    override fun addContent(): LineNumberCounts {
        val instructions = ByteCodeReader.readByteCode(attribute.code)
        verifyOffsets(instructions)
        calculateOffsetWidth(instructions)
        return instructions.map {
            addInstructionToDocument(it)
        }
    }

    private fun verifyOffsets(instructions: ArrayList<Instruction>) {
        instructions.filterIsInstance<AbstractBranchInstruction>().forEach { instruction ->
            val targetOffset = instruction.branchOffset + instruction.offset
            if (instructions.none { it.offset == targetOffset }) {
                invalidBranches.add(instruction)
            }
        }
    }

    private fun calculateOffsetWidth(instructions: List<Instruction>) {
        offsetWidth = instructions.lastOrNull()?.offset?.toString()?.length ?: 1
    }

    private fun addInstructionToDocument(instruction: Instruction): Int {
        val offset = instruction.offset
        addOffsetReference(offset)
        appendString(getPaddedValue(offset, offsetWidth), STYLE_OFFSET)
        appendString(" ", STYLE_NORMAL)

        val linkStyle = styles.addAttribute(STYLE_INSTRUCTION, ATTRIBUTE_NAME_LINK, SpecLink(instruction.opcode))
        appendString(instruction.opcode.verbose, linkStyle)

        val additionalLines = addOpcodeSpecificInfo(instruction)
        appendBatchLineFeed(STYLE_NORMAL)
        return additionalLines + 1
    }

    private fun addOffsetReference(offset: Int) {
        offsetToPosition[offset] = length
    }

    private fun addOpcodeSpecificInfo(instruction: Instruction): Int =
            when (instruction) {
                is ImmediateByteInstruction -> addImmediateByteSpecificInfo(instruction)
                is ImmediateShortInstruction -> addImmediateShortSpecificInfo(instruction)
                is AbstractBranchInstruction -> addBranchSpecificInfo(instruction)
                is TableSwitchInstruction -> addTableSwitchSpecificInfo(instruction)
                is LookupSwitchInstruction -> addLookupSwitchSpecificInfo(instruction)
                else -> 0
            }

    private fun addImmediateByteSpecificInfo(instruction: ImmediateByteInstruction): Int {
        val opcode = instruction.opcode
        val sourceOffset = instruction.offset
        val immediateByte = instruction.immediateByte

        if (opcode === Opcode.LDC) {
            addConstantPoolLink(immediateByte, sourceOffset)
        } else if (opcode === Opcode.NEWARRAY) {
            val verbose: String = try {
                NewArrayType.getFromTag(immediateByte).verbose
            } catch (e: InvalidByteCodeException) {
                "invalid array type"
            }
            appendString(" $immediateByte ($verbose)", STYLE_IMMEDIATE_VALUE)
        } else if (opcode === Opcode.BIPUSH) {
            appendString(" " + immediateByte.toByte(), STYLE_IMMEDIATE_VALUE)
        } else {
            appendString(" $immediateByte", STYLE_IMMEDIATE_VALUE)
            if (instruction is IncrementInstruction) {
                appendString(" by", STYLE_NORMAL)
                appendString(" " + instruction.incrementConst, STYLE_IMMEDIATE_VALUE)
            }
        }
        return 0
    }

    private fun addImmediateShortSpecificInfo(instruction: ImmediateShortInstruction): Int {
        val opcode = instruction.opcode
        val sourceOffset = instruction.offset
        val immediateShort = instruction.immediateShort
        if (opcode === Opcode.SIPUSH) {
            appendString(" $immediateShort", STYLE_IMMEDIATE_VALUE)
        } else {
            addConstantPoolLink(immediateShort, sourceOffset)
            if (instruction is InvokeInterfaceInstruction) {
                appendString(" count " + instruction.count, STYLE_IMMEDIATE_VALUE)
            } else if (instruction is MultianewarrayInstruction) {
                appendString(" dim " + instruction.dimensions, STYLE_IMMEDIATE_VALUE)
            }
        }
        return 0
    }

    private fun addBranchSpecificInfo(instruction: AbstractBranchInstruction): Int {
        val branchOffset = instruction.branchOffset
        val instructionOffset = instruction.offset
        addOffsetLink(branchOffset, instructionOffset)
        if (invalidBranches.contains(instruction)) {
            appendString(" [INVALID BRANCH]", STYLE_NORMAL)
        }
        return 0
    }

    private fun addTableSwitchSpecificInfo(instruction: TableSwitchInstruction): Int {
        val instructionOffset = instruction.offset
        val lowByte = instruction.lowByte
        val highByte = instruction.highByte
        val jumpOffsets = instruction.jumpOffsets

        appendString(" $lowByte to $highByte", STYLE_IMMEDIATE_VALUE)

        for (i in 0..highByte - lowByte) {
            appendString("\u0009" + (i + lowByte) + ": ", STYLE_IMMEDIATE_VALUE)
            addOffsetLink(jumpOffsets[i], instructionOffset)
            appendBatchLineFeed(STYLE_IMMEDIATE_VALUE)

        }
        appendString("\u0009default: ", STYLE_IMMEDIATE_VALUE)
        addOffsetLink(instruction.defaultOffset, instructionOffset)

        return highByte - lowByte + 2
    }

    private fun addLookupSwitchSpecificInfo(instruction: LookupSwitchInstruction): Int {

        val instructionOffset = instruction.offset
        val matchOffsetPairs = instruction.matchOffsetPairs
        val matchOffsetPairsCount = matchOffsetPairs.size

        appendString(" $matchOffsetPairsCount", STYLE_IMMEDIATE_VALUE)
        appendBatchLineFeed(STYLE_IMMEDIATE_VALUE)

        matchOffsetPairs.forEach { matchOffsetPair ->
            appendString("\u0009" + matchOffsetPair.match + ": ", STYLE_IMMEDIATE_VALUE)
            addOffsetLink(matchOffsetPair.offset, instructionOffset)
            appendBatchLineFeed(STYLE_IMMEDIATE_VALUE)
        }

        appendString("\u0009default: ", STYLE_IMMEDIATE_VALUE)
        addOffsetLink(instruction.defaultOffset, instructionOffset)

        return matchOffsetPairsCount + 1
    }

    private fun addOffsetLink(branchOffset: Int, sourceOffset: Int) {
        val totalOffset = branchOffset + sourceOffset
        val currentLinkStyle = styles.addAttribute(STYLE_LINK, ATTRIBUTE_NAME_LINK,
                OffsetLink(totalOffset, sourceOffset))
        appendString(" ", STYLE_NORMAL)
        appendString(totalOffset.toString(), currentLinkStyle)
        appendString(" (" + (if (branchOffset > 0) "+" else "") + branchOffset.toString() + ")", STYLE_IMMEDIATE_VALUE)
    }

    data class SpecLink(val opcode: Opcode) : Link
    data class OffsetLink(val targetOffset: Int, override val sourceOffset: Int) : DocumentLink

    companion object {
        val STYLE_OFFSET = style {
            foreground = getValueColor()
        }
        val STYLE_INSTRUCTION = style {
            bold = true
            attribute(ATTRIBUTE_NAME_HOVER_HIGHLIGHT, DOTTED_STROKE)
        }
        val STYLE_IMMEDIATE_VALUE = style {
            foreground = LightOrDarkColor(Color(255, 0, 255), Color(180, 80, 180))
            bold = true
        }
    }
}
