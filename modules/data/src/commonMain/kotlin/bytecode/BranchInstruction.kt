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
 * Describes an instruction that branches to a different offset.
 */

class BranchInstruction(opcode: Opcode, branchOffset: Int = 0) : AbstractBranchInstruction(opcode, branchOffset) {

    override val size: Int
        get() = super.size + 2

    override fun read(input: CountingDataInput) {
        super.read(input)

        branchOffset = input.readShort().toInt()
    }

    override fun write(output: CountingDataOutput) {
        super.write(output)

        output.writeShort(branchOffset)
    }

}
