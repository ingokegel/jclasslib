/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode

import org.gjt.jclasslib.io.CountingDataInput
import org.gjt.jclasslib.io.CountingDataOutput
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.Constant

/**
 * Base class for instructions that are followed by an immediate unsigned byte.
 * @property isWide Indicates whether the instruction is subject to a wide instruction or not.
 * @property immediateByte Immediate unsigned byte of this instruction.
 */
abstract class ImmediateByteInstruction(opcode: Opcode, override var isWide: Boolean, var immediateByte: Int) : Instruction(opcode), HasWide {

    override val size: Int
        get() = super.size + (if (isWide) 2 else 1)

    override fun read(input: CountingDataInput) {
        super.read(input)

        immediateByte = if (isWide) {
            input.readUnsignedShort()
        } else {
            input.readUnsignedByte()
        }
    }

    override fun write(output: CountingDataOutput) {
        super.write(output)

        if (isWide) {
            output.writeShort(immediateByte)
        } else {
            output.writeByte(immediateByte)
        }
    }

    override fun isConstantUsed(constant: Constant, classFile: ClassFile): Boolean {
        return opcode == Opcode.LDC && classFile.getConstantPoolIndex(constant) == immediateByte
    }
}

/**
 * Describes an instruction followed by an immediate unsigned byte.
 */
class SimpleImmediateByteInstruction(opcode: Opcode, isWide: Boolean, immediateByte: Int = 0) : ImmediateByteInstruction(opcode, isWide, immediateByte)
