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
 * Describes an instruction that is followed by an immediate unsigned byte.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
open class ImmediateByteInstruction
@JvmOverloads
constructor (
        opcode: Opcode,
        /**
         * Indicates whether the instruction is subject to a wide instruction or not.
         */
        var isWide: Boolean,
        /**
         * Immediate unsigned byte of this instruction.
         */
        var immediateByte: Int = 0

) : AbstractInstruction(opcode) {

    override val size: Int
        get() = super.size + (if (isWide) 2 else 1)

    @Throws(IOException::class)
    override fun read(input: ByteCodeInput) {
        super.read(input)

        if (isWide) {
            immediateByte = input.readUnsignedShort()
        } else {
            immediateByte = input.readUnsignedByte()
        }
    }

    @Throws(IOException::class)
    override fun write(output: ByteCodeOutput) {
        super.write(output)

        if (isWide) {
            output.writeShort(immediateByte)
        } else {
            output.writeByte(immediateByte)
        }
    }

}
