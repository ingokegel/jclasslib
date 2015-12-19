/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode

import org.gjt.jclasslib.io.ByteCodeInput
import org.gjt.jclasslib.io.ByteCodeOutput

import java.io.IOException

/**
 * Describes the invokeinterface instruction.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class InvokeInterfaceInstruction
@JvmOverloads
constructor(
        opcode: Opcode,
        immediateShort: Int = 0,
        /**
         * Argument count of this instruction.
         */
        var count: Int = 0
) : ImmediateShortInstruction(opcode, immediateShort) {

    override val size: Int
        get() = super.size + 2

    @Throws(IOException::class)
    override fun read(input: ByteCodeInput) {
        super.read(input)

        count = input.readUnsignedByte()
        // Next byte is always 0 and thus discarded
        input.readByte()
    }

    @Throws(IOException::class)
    override fun write(output: ByteCodeOutput) {
        super.write(output)

        output.writeByte(count)
        output.writeByte(0)
    }

}
