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
 * Describes the iinc instruction.
 * @property incrementConst Increment of this instruction.
 */
class IncrementInstruction
@JvmOverloads
constructor(opcode: Opcode, wide: Boolean, immediateByte: Int = 0, var incrementConst: Int = 0) :
        ImmediateByteInstruction(opcode, wide, immediateByte) {

    override val size: Int
        get() = super.size + (if (isWide) 2 else 1)

    override fun read(input: ByteCodeInput) {
        super.read(input)

        incrementConst = if (isWide) {
            input.readShort().toInt()
        } else {
            input.readByte().toInt()
        }
    }

    override fun write(output: ByteCodeOutput) {
        super.write(output)

        if (isWide) {
            output.writeShort(incrementConst)
        } else {
            output.writeByte(incrementConst)
        }
    }

}
