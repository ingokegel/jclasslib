/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode

import org.gjt.jclasslib.io.ByteCodeInput
import org.gjt.jclasslib.io.ByteCodeOutput

/**
 * Describes the tableswitch instruction.
 */
class TableSwitchInstruction(opcode: Opcode) : PaddedInstruction(opcode) {

    /**
     * Default offset of the branch of this instruction.
     */
    var defaultOffset: Int = 0

    /**
     * Lower bound for the table switch.
     */
    var lowByte: Int = 0

    /**
     * Upper bound for the table switch.
     */
    var highByte: Int = 0

    /**
     * Array of relative jump offsets for the table switch.
     */
    var jumpOffsets: IntArray = IntArray(0)

    override val size: Int
        get() = super.size + 12 + 4 * jumpOffsets.size

    override fun read(input: ByteCodeInput) {
        super.read(input)

        defaultOffset = input.readInt()
        lowByte = input.readInt()
        highByte = input.readInt()

        val numberOfOffsets = highByte - lowByte + 1
        jumpOffsets = IntArray(numberOfOffsets)

        for (i in 0 until numberOfOffsets) {
            jumpOffsets[i] = input.readInt()
        }

    }

    override fun write(output: ByteCodeOutput) {
        super.write(output)

        output.writeInt(defaultOffset)
        output.writeInt(lowByte)
        output.writeInt(highByte)

        for (jumpOffset in jumpOffsets) {
            output.writeInt(jumpOffset)
        }
    }

}
