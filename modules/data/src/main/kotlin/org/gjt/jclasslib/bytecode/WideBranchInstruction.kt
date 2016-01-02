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
 * Describes an instruction that is followed by an immediate int.
 */
class WideBranchInstruction @JvmOverloads constructor(opcode: Opcode, branchOffset: Int = 0) : AbstractBranchInstruction(opcode, branchOffset) {

    override val size: Int
        get() = super.size + 4

    override fun read(input: ByteCodeInput) {
        super.read(input)

        branchOffset = input.readInt()
    }

    override fun write(output: ByteCodeOutput) {
        super.write(output)

        output.writeInt(branchOffset)
    }

}
