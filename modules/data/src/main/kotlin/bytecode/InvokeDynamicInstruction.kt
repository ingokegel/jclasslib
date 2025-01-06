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
 * Describes the invokedynamic instruction.
 */
class InvokeDynamicInstruction(immediateShort: Int = 0) : ImmediateShortInstruction(Opcode.INVOKEDYNAMIC, immediateShort) {

    override val size: Int
        get() = super.size + 2

    override fun read(input: CountingDataInput) {
        super.read(input)

        // The next two bytes are always 0 and thus discarded
        input.readUnsignedShort()
    }

    override fun write(output: CountingDataOutput) {
        super.write(output)

        output.writeShort(0)
    }

}
