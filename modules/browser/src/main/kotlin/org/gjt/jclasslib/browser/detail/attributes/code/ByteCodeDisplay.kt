/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.bytecode.*
import org.gjt.jclasslib.io.ByteCodeReader
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.util.BatchDocument
import java.awt.Color
import java.io.IOException
import java.util.*
import javax.swing.text.*

class ByteCodeDisplay(private val detailPane: ByteCodeDetailPane, private val styles: StyleContext, private val attribute: CodeAttribute, private val classFile: ClassFile) : BatchDocument(styles) {

    private val offsetToPosition = HashMap<Int, Int>()
    private val lineStartPositions = ArrayList<Int>()
    val opcodeCounterDocument = BatchDocument(styles)
    var opcodeCounterWidth: Int = 0
        private set
    private var offsetWidth: Int = 0
    private val invalidBranches = HashSet<Instruction>()

    init {
        putProperty("tabSize", 4)
        setupDocument()
    }

    fun getPosition(offset: Int): Int = offsetToPosition[offset] ?: 0

    fun getLineStartPosition(lineNumber: Int) = lineStartPositions.getOrElse(lineNumber - 1) { if (it < 0) 0 else length }

    private fun setupDocument() {
        try {
            val instructions = ByteCodeReader.readByteCode(attribute.code)
            verifyOffsets(instructions)
            calculateOffsetWidth(instructions)
            detailPane.setCurrentInstructions(instructions)
            val instructionLineCounts = instructions.map {
                addInstructionToDocument(it)
            }
            processBatchUpdates(0)
            createOpcodeCounterDocument(instructionLineCounts)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    private fun verifyOffsets(instructions: ArrayList<Instruction>) {
        instructions.forEachIndexed { i, instruction ->
            if (instruction is AbstractBranchInstruction) {
                var branchOffset = instruction.branchOffset
                var targetDistance = 0
                if (branchOffset > 0) {
                    branchOffset -= instruction.size
                    while (branchOffset > 0 && i + targetDistance + 1 < instructions.size) {
                        ++targetDistance
                        branchOffset -= instructions[i + targetDistance].size
                    }
                } else {
                    while (branchOffset < 0 && i + targetDistance > 0) {
                        --targetDistance
                        branchOffset += instructions[i + targetDistance].size
                    }
                }
                if (branchOffset != 0) {
                    invalidBranches.add(instruction)
                }
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
        appendString(" " + instruction.opcode.verbose, STYLE_INSTRUCTION)
        val additionalLines = addOpcodeSpecificInfo(instruction)
        appendBatchLineFeed(STYLE_NORMAL)
        return additionalLines + 1
    }

    private fun addOffsetReference(offset: Int) {
        offsetToPosition.put(offset, length)
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
            val verbose: String
            try {
                verbose = NewArrayType.getFromTag(immediateByte).verbose
            } catch (e: InvalidByteCodeException) {
                verbose = "invalid array type"
            }
            appendString(" $immediateByte ($verbose)", STYLE_IMMEDIATE_VALUE)
        } else if (opcode === Opcode.BIPUSH) {
            appendString(" " + immediateByte.toByte(), STYLE_IMMEDIATE_VALUE)
        } else {
            appendString(" " + immediateByte, STYLE_IMMEDIATE_VALUE)
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
            appendString(" " + immediateShort, STYLE_IMMEDIATE_VALUE)
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

        appendString(" " + matchOffsetPairsCount, STYLE_IMMEDIATE_VALUE)
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

    private fun addConstantPoolLink(constantPoolIndex: Int, sourceOffset: Int) {

        val currentLinkStyle = styles.addAttribute(STYLE_LINK, ATTRIBUTE_NAME_LINK,
                DocumentLink(constantPoolIndex, sourceOffset, DocumentLinkType.CONSTANT_POOL_LINK))

        appendString(" ", STYLE_NORMAL)
        appendString("#" + constantPoolIndex, currentLinkStyle)
        try {
            val name = classFile.getConstantPoolEntryName(constantPoolIndex)
            if (name.length > 0) {
                appendString(" <$name>", STYLE_SMALL)
            }
        } catch (ex: InvalidByteCodeException) {
            appendString(" [INVALID]", STYLE_SMALL)
        }
    }

    private fun addOffsetLink(branchOffset: Int, sourceOffset: Int) {
        val totalOffset = branchOffset + sourceOffset
        val currentLinkStyle = styles.addAttribute(STYLE_LINK, ATTRIBUTE_NAME_LINK,
                DocumentLink(totalOffset, sourceOffset, DocumentLinkType.OFFSET_LINK))
        appendString(" ", STYLE_NORMAL)
        appendString(totalOffset.toString(), currentLinkStyle)
        appendString(" (" + (if (branchOffset > 0) "+" else "") + branchOffset.toString() + ")", STYLE_IMMEDIATE_VALUE)
    }

    override fun appendBatchLineFeed(attributes: AttributeSet) {
        super.appendBatchLineFeed(attributes)
        lineStartPositions.add(length)
    }

    private fun appendString(string: String, attributes: AttributeSet) {
        try {
            appendBatchString(string, attributes)
        } catch (ex: BadLocationException) {
            ex.printStackTrace()
        }
    }

    private fun createOpcodeCounterDocument(instructionLineCounts: List<Int>) {
        val numberOfOpcodes = instructionLineCounts.size
        opcodeCounterWidth = (numberOfOpcodes - 1).toString().length
        try {
            for (i in 0..numberOfOpcodes - 1) {
                opcodeCounterDocument.appendBatchString(getPaddedValue(i, opcodeCounterWidth), STYLE_LINE_NUMBER)
                for (j in 0..instructionLineCounts[i] - 1) {
                    opcodeCounterDocument.appendBatchLineFeed(STYLE_NORMAL)
                }
            }
            opcodeCounterDocument.processBatchUpdates(0)
        } catch (ex: BadLocationException) {
            ex.printStackTrace()
        }
    }

    class DocumentLink(val index: Int, val sourceOffset: Int, val type: DocumentLinkType)

    enum class DocumentLinkType {
        CONSTANT_POOL_LINK, OFFSET_LINK
    }

    companion object {
        val ATTRIBUTE_NAME_LINK = "attributeLink"
        val STYLE_NORMAL: MutableAttributeSet
        val STYLE_SMALL: MutableAttributeSet
        val STYLE_LINK: MutableAttributeSet
        val STYLE_OFFSET: MutableAttributeSet
        val STYLE_INSTRUCTION: MutableAttributeSet
        val STYLE_IMMEDIATE_VALUE: MutableAttributeSet
        val STYLE_LINE_NUMBER: MutableAttributeSet

        private val LINE_NUMBERS_FONT_DIFF = 2

        init {
            STYLE_NORMAL = SimpleAttributeSet()

            STYLE_LINK = SimpleAttributeSet().apply {
                StyleConstants.setForeground(this, Color(0, 128, 0))
                StyleConstants.setBold(this, true)
                StyleConstants.setUnderline(this, true)
            }

            STYLE_OFFSET = SimpleAttributeSet().apply {
                StyleConstants.setForeground(this, Color(128, 0, 0))
            }

            STYLE_INSTRUCTION = SimpleAttributeSet().apply {
                StyleConstants.setBold(this, true)
            }

            STYLE_IMMEDIATE_VALUE = SimpleAttributeSet().apply {
                StyleConstants.setForeground(this, Color.magenta)
                StyleConstants.setBold(this, true)
            }

            STYLE_LINE_NUMBER = SimpleAttributeSet().apply {
                StyleConstants.setForeground(this, Color(128, 128, 128))
                StyleConstants.setFontSize(this, StyleConstants.getFontSize(this) - LINE_NUMBERS_FONT_DIFF)
            }

            STYLE_SMALL = SimpleAttributeSet().apply {
                StyleConstants.setFontSize(this, StyleConstants.getFontSize(this) - 1)
            }

        }

        private fun getPaddedValue(number: Int, width: Int) = StringBuilder().apply {
            val value = number.toString()
            val valueLength = value.length
            for (i in valueLength..width - 1) {
                append(' ')
            }
            append(value)
        }.toString()
    }
}
