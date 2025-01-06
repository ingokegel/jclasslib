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
 * Describes the multianewarray instruction.
 */
class MultianewarrayInstruction(immediateShort: Int = 0) : ImmediateShortInstruction(Opcode.MULTIANEWARRAY, immediateShort) {

    /**
     * Number of dimensions for the new array.
     */
    var dimensions: Int = 0

    override val size: Int
        get() = super.size + 1

    override fun read(input: CountingDataInput) {
        super.read(input)

        dimensions = input.readUnsignedByte()
    }

    override fun write(output: CountingDataOutput) {
        super.write(output)

        output.writeByte(dimensions)
    }

}
