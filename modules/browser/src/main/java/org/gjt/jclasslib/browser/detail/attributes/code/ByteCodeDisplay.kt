/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener
import org.gjt.jclasslib.bytecode.*
import org.gjt.jclasslib.io.ByteCodeReader
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.font.FontRenderContext
import java.awt.font.TextAttribute
import java.awt.font.TextLayout
import java.io.IOException
import java.text.AttributedString
import java.util.*
import javax.swing.*
import javax.swing.border.Border
import javax.swing.border.EmptyBorder

class ByteCodeDisplay(private val detailPane: ByteCodeDetailPane) : JPanel(), Scrollable {

    var codeAttribute: CodeAttribute? = null
        private set
    private var classFile: ClassFile? = null

    private var offsetWidth: Int = 0
    private var offsetBlank: String? = null
    private val offsetToLine = HashMap<Int, Int>()
    private val lines = ArrayList<AttributedString>()
    private val textLines = ArrayList<String>()
    private val lineToLink = HashMap<Int, BytecodeLink>()
    private val invalidBranches = HashSet<Instruction>()

    private val currentLineCache = LinkedList<LineCacheEntry>()
    private var currentHeight: Float = 0.toFloat()
    private var currentWidth: Float = 0.toFloat()

    private var textLayouts = arrayOf<TextLayout?>()

    var lineHeight: Int = 0
        private set
    var ascent: Int = 0
        private set

    private var characterWidth: Int = 0

    private val frc: FontRenderContext
        get() = (graphics as Graphics2D).fontRenderContext

    init {
        setupComponent()
        setupEventHandlers()
    }

    override fun getPreferredScrollableViewportSize() = null

    override fun getScrollableUnitIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int {
        if (orientation == SwingConstants.HORIZONTAL) {
            return 10
        } else if (lineHeight == 0) {
            return 1
        } else {
            val currentY = (parent as JViewport).viewPosition.y
            val line = 1f * (currentY - MARGIN_Y) / lineHeight
            val targetLine = (if (direction < 0) Math.floor(line.toDouble()) - 1 else Math.ceil(line.toDouble()) + 1).toInt()
            val targetY = MARGIN_Y + targetLine * lineHeight + 1
            return Math.abs(currentY - targetY)
        }
    }

    override fun getScrollableBlockIncrement(visibleRect: Rectangle, orientation: Int, direction: Int): Int {
        val viewport = parent as JViewport
        if (orientation == SwingConstants.HORIZONTAL) {
            return viewport.width
        } else if (lineHeight == 0) {
            return 1
        } else {
            val currentY = viewport.viewPosition.y
            val rawTargetY = currentY + if (direction < 0) -1 else 1 * viewport.height
            val line = 1f * (rawTargetY - MARGIN_Y) / lineHeight
            val targetLine = (if (direction < 0) Math.ceil(line.toDouble()) else Math.floor(line.toDouble())).toInt()
            val targetY = MARGIN_Y + targetLine * lineHeight + 1
            return Math.abs(currentY - targetY)
        }
    }

    override fun getScrollableTracksViewportWidth() = false
    override fun getScrollableTracksViewportHeight() = false

    val lineCount: Int
        get() = lines.size

    fun setCodeAttribute(codeAttribute: CodeAttribute, classFile: ClassFile) {
        this.codeAttribute = codeAttribute
        this.classFile = classFile
        setupTextLayouts()
        invalidate()
    }

    fun link(point: Point) {
        val link = getLink(point) ?: return
        updateHistory(link.sourceOffset)

        if (link is ConstantPoolLink) {
            ConstantPoolHyperlinkListener.link(detailPane.services, link.cpIndex)
        } else if (link is OffsetLink) {
            val targetOffset = link.targetOffset
            scrollToOffset(targetOffset)
            updateHistory(targetOffset)
        }
    }

    fun isLink(point: Point) = getLink(point) != null

    fun scrollToOffset(offset: Int) {
        val line = offsetToLine[offset] ?: return
        val target = Rectangle(0, line * lineHeight + MARGIN_Y + 1, 10, parent.height)
        scrollRectToVisible(target)
    }

    fun copyToClipboard() {
        val stringSelection = StringSelection(clipboardText)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, stringSelection)
    }

    val clipboardText: String
        get() = StringBuilder().apply {
            for (line in textLines) {
                append(line)
                append('\n')
            }
        }.toString()

    override fun paintComponent(graphics: Graphics) {
        if (lineHeight == 0) {
            return
        }
        (graphics as Graphics2D).apply {
            translate(MARGIN_X, MARGIN_Y)
            val oldPaint = paint
            paint = Color.WHITE
            fill(clipBounds)
            paint = oldPaint
            drawLines(this)
            translate(-MARGIN_X, -MARGIN_Y)
        }
    }

    private fun drawLines(g: Graphics2D) {
        val clipBounds = g.clipBounds
        val startLine = Math.max(0, clipBounds.y / lineHeight - 1)
        val endLine = Math.min(lines.size, (clipBounds.y + clipBounds.height) / lineHeight + 1)
        for (i in startLine..endLine - 1) {
            val textLayout = getOrCreateTextLayout(i)
            textLayout.draw(g, 0f, i * lineHeight + textLayout.ascent)
        }
    }

    private fun getOrCreateTextLayout(i: Int): TextLayout {
        val textLayout: TextLayout? = textLayouts[i]
        if (textLayout == null) {
            return TextLayout(lines[i].iterator, frc).apply {
                textLayouts[i] = this
            }
        } else {
            return textLayout
        }
    }

    private fun setupComponent() {
        border = BORDER
        isDoubleBuffered = false
        isOpaque = false
    }

    private fun setupEventHandlers() {
    }

    private fun getLink(point: Point): BytecodeLink? {
        if (lineHeight == 0) {
            return null
        }
        val x = point.x - MARGIN_X
        val y = point.y - MARGIN_Y
        val line = y / lineHeight
        val link = lineToLink[line] ?: return null

        val textLayout = getOrCreateTextLayout(line)
        val textHitInfo = textLayout.hitTestChar(x.toFloat(), (y - line * lineHeight).toFloat())
        val charIndex = textHitInfo.charIndex
        if (charIndex >= link.startCharIndex && charIndex < link.endCharIndex) {
            return link
        } else {
            return null
        }
    }

    private fun updateHistory(offset: Int) {
        val services = detailPane.services
        val treePath = services.browserComponent.treePane.tree.selectionPath

        val history = services.browserComponent.history
        history.updateHistory(treePath, offset)
    }

    private fun setupTextLayouts() {
        lineHeight = 0
        currentHeight = 0f
        currentWidth = 0f
        textLines.clear()
        lines.clear()
        textLayouts = arrayOf()
        offsetToLine.clear()
        lineToLink.clear()
        invalidBranches.clear()

        codeAttribute?.code?.let {code ->
            try {
                val instructions = ByteCodeReader.readByteCode(code)
                verifyOffsets(instructions)
                calculateOffsetWidth(instructions)
                detailPane.setCurrentInstructions(instructions)
                instructions.forEach { instruction -> addInstructionToDocument(instruction) }
                textLayouts = arrayOfNulls(lines.size)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        preferredSize = Dimension(currentWidth.toInt() + 2 * MARGIN_X, currentHeight.toInt() + 2 * MARGIN_Y)
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
        offsetBlank = StringBuilder(offsetWidth).apply {
            for (i in 0..offsetWidth - 1) {
                append(' ')
            }
        }.toString()
    }

    private fun addInstructionToDocument(instruction: Instruction) {
        val offset = instruction.offset
        addOffsetReference(offset)
        appendString(getPaddedValue(offset, offsetWidth), STYLE_OFFSET)
        appendString(" " + instruction.opcode.verbose, STYLE_INSTRUCTION)
        addOpcodeSpecificInfo(instruction)
        newLine()
    }

    private fun addOffsetReference(offset: Int) {
        offsetToLine.put(offset, currentLine)
    }

    private fun addOpcodeSpecificInfo(instruction: Instruction) {
        when (instruction) {
            is ImmediateByteInstruction -> addImmediateByteSpecificInfo(instruction)
            is ImmediateShortInstruction -> addImmediateShortSpecificInfo(instruction)
            is AbstractBranchInstruction -> addBranchSpecificInfo(instruction)
            is TableSwitchInstruction -> addTableSwitchSpecificInfo(instruction)
            is LookupSwitchInstruction -> addLookupSwitchSpecificInfo(instruction)
        }
    }

    private fun addImmediateByteSpecificInfo(instruction: ImmediateByteInstruction) {
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
    }

    private fun addImmediateShortSpecificInfo(instruction: ImmediateShortInstruction) {
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
    }

    private fun addBranchSpecificInfo(instruction: AbstractBranchInstruction) {
        val branchOffset = instruction.branchOffset
        val instructionOffset = instruction.offset
        addOffsetLink(branchOffset, instructionOffset)
        if (invalidBranches.contains(instruction)) {
            appendString(" [INVALID BRANCH]", STYLE_NORMAL)
        }
    }

    private fun addTableSwitchSpecificInfo(instruction: TableSwitchInstruction) {
        val instructionOffset = instruction.offset
        val lowByte = instruction.lowByte
        val highByte = instruction.highByte
        val jumpOffsets = instruction.jumpOffsets
        appendString(" $lowByte to $highByte", STYLE_IMMEDIATE_VALUE)
        newLine()

        for (i in 0..highByte - lowByte) {
            appendString(offsetBlank + TAB_STRING + (i + lowByte) + ": ", STYLE_IMMEDIATE_VALUE)
            addOffsetLink(jumpOffsets[i], instructionOffset)
            newLine()

        }
        appendString(offsetBlank + TAB_STRING + "default: ", STYLE_IMMEDIATE_VALUE)
        addOffsetLink(instruction.defaultOffset, instructionOffset)
    }

    private fun addLookupSwitchSpecificInfo(instruction: LookupSwitchInstruction) {
        val instructionOffset = instruction.offset
        val matchOffsetPairs = instruction.matchOffsetPairs
        val matchOffsetPairsCount = matchOffsetPairs.size
        appendString(" " + matchOffsetPairsCount, STYLE_IMMEDIATE_VALUE)
        newLine()

        matchOffsetPairs.forEach { matchOffsetPair ->
            appendString(offsetBlank + TAB_STRING + matchOffsetPair.match + ": ", STYLE_IMMEDIATE_VALUE)
            addOffsetLink(matchOffsetPair.offset, instructionOffset)
            newLine()
        }
        appendString(offsetBlank + TAB_STRING + "default: ", STYLE_IMMEDIATE_VALUE)
        addOffsetLink(instruction.defaultOffset, instructionOffset)
    }

    private fun addConstantPoolLink(constantPoolIndex: Int, sourceOffset: Int) {
        appendString(" ", STYLE_NORMAL)
        val startCharIndex = currentCharIndex
        appendString("#" + constantPoolIndex, STYLE_LINK)
        val endCharIndex = currentCharIndex
        lineToLink.put(currentLine, ConstantPoolLink(startCharIndex, endCharIndex, sourceOffset, constantPoolIndex))

        try {
            val name = classFile?.getConstantPoolEntryName(constantPoolIndex) ?: ""
            if (name.length > 0) {
                appendString(" <$name>", STYLE_SMALL)
            }
        } catch (ex: InvalidByteCodeException) {
            appendString(" [INVALID]", STYLE_SMALL)
        }
    }

    private fun addOffsetLink(branchOffset: Int, sourceOffset: Int) {
        val targetOffset = branchOffset + sourceOffset
        appendString(" ", STYLE_NORMAL)
        val startCharIndex = currentCharIndex
        appendString(targetOffset.toString(), STYLE_LINK)
        val endCharIndex = currentCharIndex
        lineToLink.put(currentLine, OffsetLink(startCharIndex, endCharIndex, sourceOffset, targetOffset))
        appendString(" (" + if (branchOffset > 0) "+" else "" + branchOffset.toString() + ")", STYLE_IMMEDIATE_VALUE)
    }

    private val currentCharIndex: Int
        get() = currentLineCache.sumBy { it.text.length }

    private val currentLine: Int
        get() = lines.size

    private fun appendString(text: String, attributes: Map<TextAttribute, Any>) {
        currentLineCache.add(LineCacheEntry(text, attributes))
    }

    private fun newLine() {
        val text = currentLineText
        val attrString = AttributedString(text, STYLE_BASE)
        var startCharIndex = 0
        for (entry in currentLineCache) {
            val endCharIndex = startCharIndex + entry.text.length
            attrString.addAttributes(entry.attributes, startCharIndex, endCharIndex)
            startCharIndex = endCharIndex
        }
        lines.add(attrString)
        textLines.add(text)

        if (lineHeight == 0) {
            TextLayout(attrString.iterator, frc).let {
                lineHeight = (it.ascent + it.descent + it.leading).toInt()
                ascent = it.ascent.toInt()
            }
            TextLayout("0", STYLE_BASE, frc).let {
                characterWidth = it.advance.toInt()
            }
        }
        currentHeight += lineHeight.toFloat()
        currentWidth = Math.max(currentWidth, (characterWidth * text.length).toFloat())

        currentLineCache.clear()
    }

    private val currentLineText: String
        get() = StringBuilder(currentCharIndex).apply {
            for (entry in currentLineCache) {
                append(entry.text)
            }
        }.toString()


    private class LineCacheEntry(val text: String, val attributes: Map<TextAttribute, Any>)

    private open class BytecodeLink(val startCharIndex: Int, val endCharIndex: Int, var sourceOffset: Int)
    private class ConstantPoolLink(startCharIndex: Int, endCharIndex: Int, sourceOffset: Int, val cpIndex: Int) : BytecodeLink(startCharIndex, endCharIndex, sourceOffset)
    private class OffsetLink(startCharIndex: Int, endCharIndex: Int, sourceOffset: Int, val targetOffset: Int) : BytecodeLink(startCharIndex, endCharIndex, sourceOffset)

    companion object {

        val MARGIN_X = 3
        val MARGIN_Y = 3
        val BORDER: Border = EmptyBorder(MARGIN_Y, MARGIN_X, MARGIN_Y, MARGIN_X)

        private val BASE_FONT = UIManager.getFont("TextArea.font")
        private val STYLE_BASE = mapOf<TextAttribute, Any>(
                TextAttribute.FAMILY to "MonoSpaced",
                TextAttribute.SIZE to BASE_FONT.size.toFloat()
        )
        private val STYLE_NORMAL = mapOf<TextAttribute, Any>()
        private val STYLE_SMALL = mapOf<TextAttribute, Any>(
                TextAttribute.SIZE to (BASE_FONT.size - 1).toFloat()
        )
        private val STYLE_LINK = mapOf<TextAttribute, Any>(
                TextAttribute.FOREGROUND to Color(0, 128, 0),
                TextAttribute.WEIGHT to TextAttribute.WEIGHT_BOLD,
                TextAttribute.UNDERLINE to TextAttribute.UNDERLINE_ON
        )
        private val STYLE_OFFSET = mapOf<TextAttribute, Any>(
                TextAttribute.FOREGROUND to Color(128, 0, 0)
        )
        private val STYLE_INSTRUCTION = mapOf<TextAttribute, Any>(
                TextAttribute.WEIGHT to TextAttribute.WEIGHT_BOLD
        )
        private val STYLE_IMMEDIATE_VALUE = mapOf<TextAttribute, Any>(
                TextAttribute.FOREGROUND to Color.magenta,
                TextAttribute.WEIGHT to TextAttribute.WEIGHT_BOLD
        )
        private val TAB_STRING = "        "

        fun getPaddedValue(number: Int, width: Int) = StringBuilder().apply {
            val value = number.toString()
            val valueLength = value.length
            for (i in valueLength..width - 1) {
                append(' ')
            }
            append(value)
        }.toString()
    }
}
