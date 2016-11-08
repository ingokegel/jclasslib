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
import java.awt.BasicStroke
import java.awt.Color
import java.io.IOException
import java.util.*
import javax.swing.event.DocumentEvent
import javax.swing.text.*

class ByteCodeDocument(private val styles: StyleContext, private val attribute: CodeAttribute, private val classFile: ClassFile) : BatchDocument(styles) {

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

    fun modifyDocument(block: DocumentModification.() -> Unit) {
        writeLock()
        try {
            val documentModification = DocumentModification()
            documentModification.block()
            documentModification.modifiedRanges.forEach { range ->
                fireChangedUpdate(DefaultDocumentEvent(range.start, range.last - range.first, DocumentEvent.EventType.CHANGE))
            }
        } finally {
            writeUnlock()
        }
    }

    private fun setupDocument() {
        try {
            val instructions = ByteCodeReader.readByteCode(attribute.code)
            verifyOffsets(instructions)
            calculateOffsetWidth(instructions)
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
        appendString(" ", STYLE_NORMAL)

        val linkStyle = styles.addAttribute(STYLE_INSTRUCTION, ATTRIBUTE_NAME_LINK, SpecLink(instruction.opcode))
        appendString(instruction.opcode.verbose, linkStyle)

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
            val verbose: String = try {
                NewArrayType.getFromTag(immediateByte).verbose
            } catch (e: InvalidByteCodeException) {
                "invalid array type"
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
                ConstantPoolLink(constantPoolIndex, sourceOffset))

        appendString(" ", STYLE_NORMAL)
        appendString("#" + constantPoolIndex, currentLinkStyle)
        try {
            val name = classFile.getConstantPoolEntryName(constantPoolIndex)
            if (name.isNotEmpty()) {
                appendString(" <$name>", STYLE_SMALL)
            }
        } catch (ex: InvalidByteCodeException) {
            appendString(" [INVALID]", STYLE_SMALL)
        }
    }

    private fun addOffsetLink(branchOffset: Int, sourceOffset: Int) {
        val totalOffset = branchOffset + sourceOffset
        val currentLinkStyle = styles.addAttribute(STYLE_LINK, ATTRIBUTE_NAME_LINK,
                OffsetLink(totalOffset, sourceOffset))
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

    private fun getPaddedValue(number: Int, width: Int) = StringBuilder().apply {
        val value = number.toString()
        val valueLength = value.length
        for (i in valueLength..width - 1) {
            append(' ')
        }
        append(value)
    }.toString()

    interface Link
    interface DocumentLink : Link {
        val sourceOffset: Int
    }

    data class OffsetLink(val targetOffset: Int, override val sourceOffset: Int) : DocumentLink
    data class ConstantPoolLink(val constantPoolIndex: Int, override val sourceOffset: Int) : DocumentLink
    data class SpecLink(val opcode: Opcode) : Link

    class DocumentModification {
        var modifiedRanges = mutableListOf<IntRange>()

        fun modifiedRange(range: IntRange) {
            modifiedRanges.add(range)
        }

        fun modifiedElement(element: AbstractElement) {
            modifiedRange(element.startOffset..element.endOffset)
        }
    }

    companion object {
        val ATTRIBUTE_NAME_LINK = "attributeLink"
        val ATTRIBUTE_NAME_HOVER_HIGHLIGHT = "hoverHighlight"

        val ACTIVE_LINK_COLOR = Color(196, 0, 0)

        private val DOTTED_STROKE = BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0.toFloat(), floatArrayOf(3.toFloat(), 4.toFloat()), 0.0f)

        val STYLE_NORMAL = style()
        val STYLE_SMALL = style {
            fontSize -= 1
        }
        val STYLE_LINK = style {
            foreground = Color(0, 128, 0)
            bold = true
            underline = true
        }
        val STYLE_OFFSET = style {
            foreground = Color(128, 0, 0)
        }
        val STYLE_INSTRUCTION = style {
            bold = true
            attribute(ATTRIBUTE_NAME_HOVER_HIGHLIGHT, DOTTED_STROKE)
        }
        val STYLE_IMMEDIATE_VALUE = style {
            foreground = Color.MAGENTA
            bold = true
        }
        val STYLE_LINE_NUMBER = style {
            foreground = Color(128, 128, 128)
            fontSize -= 2
        }

        fun style(init: StyleBuilder.() -> Unit = {}): AttributeSet {
            val styleBuilder = StyleBuilder()
            // The next line explicitly sets the font size on the attribute set, this is needed for HiDPI displays
            styleBuilder.fontSize += 0
            styleBuilder.init()
            return styleBuilder.attributeSet
        }

        class StyleBuilder {
            val attributeSet = SimpleAttributeSet()

            var fontSize: Int
                get() = StyleConstants.getFontSize(attributeSet)
                set(fontSize) {
                    StyleConstants.setFontSize(attributeSet, fontSize)
                }

            var foreground: Color
                get() = StyleConstants.getForeground(attributeSet)
                set(color) {
                    StyleConstants.setForeground(attributeSet, color)
                }

            var bold: Boolean
                get() = StyleConstants.isBold(attributeSet)
                set(bold) {
                    StyleConstants.setBold(attributeSet, bold)
                }

            var underline: Boolean
                get() = StyleConstants.isUnderline(attributeSet)
                set(bold) {
                    StyleConstants.setUnderline(attributeSet, bold)
                }

            fun attribute(name: String, value: Any) {
                attributeSet.addAttribute(name, value)
            }
        }
    }
}
