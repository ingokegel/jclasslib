/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFile
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an entry in a BootstrapMethods attribute structure.
 */
class StackMapFrameEntry(private val classFile: ClassFile) : SubStructure() {

    /**
     * Frame tag
     */
    var tag: Int = 0

    /**
     * Frame type
     */
    var frameType: StackFrameType = StackFrameType.SAME

    /**
     * Offset delta.
     */
    var offsetDelta: Int = 0

    /**
     * Offset.
     */
    var offset: Int = 0

    /**
     * Local verification items. No consistency check will be performed.
     */
    var localItems = emptyArray<VerificationTypeInfoEntry>()

    /**
     * Stack verification items. No consistency check will be performed.
     */
    var stackItems = emptyArray<VerificationTypeInfoEntry>()

    override fun readData(input: DataInput) {
        tag = input.readUnsignedByte()
        frameType = StackFrameType.getFromTag(tag)
        when (frameType) {
            StackFrameType.SAME -> offsetDelta = tag
            StackFrameType.SAME_LOCALS_1_STACK_ITEM -> readOneStackItem(input)
            StackFrameType.SAME_LOCALS_1_STACK_ITEM_EXT -> readOneStackItemExt(input)
            StackFrameType.CHOP -> readChop(input)
            StackFrameType.SAME_EXT -> readSameExt(input)
            StackFrameType.APPEND -> readAppend(input)
            StackFrameType.FULL -> readFull(input)
        }
    }

    private fun readOneStackItem(input: DataInput) {
        offsetDelta = tag - 64
        stackItems = arrayOf(readVerificationTypeInfoEntry(input))
    }

    private fun readVerificationTypeInfoEntry(input: DataInput): VerificationTypeInfoEntry {
        val tag = input.readUnsignedByte()
        val verificationType = VerificationType.getFromTag(tag)
        return verificationType.createEntry(classFile).apply {
            this.read(input)
        }
    }

    private fun readOneStackItemExt(input: DataInput) {
        offsetDelta = input.readUnsignedShort()
        stackItems = arrayOf(readVerificationTypeInfoEntry(input))
    }

    private fun readChop(input: DataInput) {
        offsetDelta = input.readUnsignedShort()
    }

    private fun readSameExt(input: DataInput) {
        offsetDelta = input.readUnsignedShort()
    }

    private fun readAppend(input: DataInput) {
        offsetDelta = input.readUnsignedShort()
        val numLocals = tag - 251
        localItems = readEntries(input, numLocals)
    }

    private fun readFull(input: DataInput) {
        offsetDelta = input.readUnsignedShort()
        val numLocals = input.readUnsignedShort()
        localItems = readEntries(input, numLocals)
        val numStacks = input.readUnsignedShort()
        stackItems = readEntries(input, numStacks)
    }

    private fun readEntries(input: DataInput, numLocals: Int) = Array(numLocals) {
        readVerificationTypeInfoEntry(input)
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(tag)
        when (frameType) {
            StackFrameType.SAME -> Unit
            StackFrameType.SAME_LOCALS_1_STACK_ITEM -> writeOneStackItem(output)
            StackFrameType.SAME_LOCALS_1_STACK_ITEM_EXT -> writeOneStackItemExt(output)
            StackFrameType.CHOP -> writeChop(output)
            StackFrameType.SAME_EXT -> writeSameExt(output)
            StackFrameType.APPEND -> writeAppend(output)
            StackFrameType.FULL -> writeFull(output)
        }
    }

    private fun writeOneStackItem(out: DataOutput) {
        stackItems[0].write(out)
    }

    private fun writeOneStackItemExt(out: DataOutput) {
        out.writeShort(offsetDelta)
        stackItems[0].write(out)
    }

    private fun writeChop(out: DataOutput) {
        out.writeShort(offsetDelta)
    }

    private fun writeSameExt(out: DataOutput) {
        out.writeShort(offsetDelta)
    }

    private fun writeAppend(out: DataOutput) {
        out.writeShort(offsetDelta)
        for (localItem in localItems) {
            localItem.write(out)
        }
    }

    private fun writeFull(out: DataOutput) {
        out.writeShort(offsetDelta)
        out.writeShort(localItems.size)
        for (localItem in localItems) {
            localItem.write(out)
        }
        out.writeShort(stackItems.size)
        for (stackItem in stackItems) {
            stackItem.write(out)
        }
    }

    override val debugInfo: String
        get() = "of type $frameType"

    /**
     * Returns the verbose representation for display in the UI
     */
    val verbose: String
        get() {
            val buffer = StringBuilder()
            buffer.append("<b>").append(frameType).append("</b> (").append(tag).append(')')
            appendOffset(buffer)
            when (frameType) {
                StackFrameType.SAME, StackFrameType.CHOP, StackFrameType.SAME_EXT -> Unit
                StackFrameType.SAME_LOCALS_1_STACK_ITEM, StackFrameType.SAME_LOCALS_1_STACK_ITEM_EXT -> appendStack(buffer)
                StackFrameType.APPEND -> appendLocals(buffer)
                StackFrameType.FULL -> {
                    appendLocals(buffer)
                    appendStack(buffer)
                }
            }
            return buffer.toString().replace("\n", "<br>").replace(" ", "&nbsp;")
        }

    private fun appendOffset(buffer: StringBuilder) {
        buffer.append(", Offset: ").append(offset).append(" (+").append(offsetDelta).append(")")
    }

    private fun appendStack(buffer: StringBuilder) {
        buffer.append("\n    Stack verifications:\n")
        appendEntries(stackItems, buffer)
    }

    private fun appendLocals(buffer: StringBuilder) {
        buffer.append("\n    Local verifications:\n")
        appendEntries(localItems, buffer)
    }

    private fun appendEntries(entries: Array<VerificationTypeInfoEntry>, buffer: StringBuilder) {
        entries.forEachIndexed { i, entry ->
            buffer.append("        ")
            entry.appendTo(buffer)
            if (i < entries.size - 1) {
                buffer.append("\n")
            }
        }
    }

    /**
     * Returns the bytecode length of the entry
     */
    override val length: Int
        get() = when (frameType) {
            StackFrameType.SAME -> 1
            StackFrameType.SAME_LOCALS_1_STACK_ITEM -> 1 + stackItems.totalLength()
            StackFrameType.SAME_LOCALS_1_STACK_ITEM_EXT -> 3 + stackItems.totalLength()
            StackFrameType.CHOP, StackFrameType.SAME_EXT -> 3
            StackFrameType.APPEND -> 3 + localItems.totalLength()
            StackFrameType.FULL -> 7 + localItems.totalLength() + stackItems.totalLength()
        }

    private fun Array<VerificationTypeInfoEntry>.totalLength(): Int = this.sumBy { it.length }

}
