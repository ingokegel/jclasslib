/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode

import org.gjt.jclasslib.io.CountingDataInput
import org.gjt.jclasslib.io.CountingDataOutput

/**
 * Describes the iinc instruction.
 * @property incrementConst Increment of this instruction.
 */
class IncrementInstruction(wide: Boolean, immediateByte: Int = 0, var incrementConst: Int = 0) :
        ImmediateByteInstruction(Opcode.IINC, wide, immediateByte) {

    override val size: Int
        get() = super.size + (if (isWide) 2 else 1)

    override fun read(input: CountingDataInput) {
        super.read(input)

        incrementConst = if (isWide) {
            input.readShort().toInt()
        } else {
            input.readByte().toInt()
        }
    }

    override fun write(output: CountingDataOutput) {
        super.write(output)

        if (isWide) {
            output.writeShort(incrementConst)
        } else {
            output.writeByte(incrementConst)
        }
    }

}
