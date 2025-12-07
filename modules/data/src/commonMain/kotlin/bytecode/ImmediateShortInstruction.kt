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
 * Describes an instruction followed by an immediate unsigned short.
 * @property immediateShort Immediate unsigned short of this instruction.
 */
abstract class ImmediateShortInstruction(opcode: Opcode, var immediateShort: Int) : Instruction(opcode) {

    override val size: Int
        get() = super.size + 2

    override fun read(input: CountingDataInput) {
        super.read(input)

        immediateShort = input.readUnsignedShort()
    }

    override fun write(output: CountingDataOutput) {
        super.write(output)

        output.writeShort(immediateShort)
    }

    override fun isConstantUsed(constant: Constant, classFile: ClassFile): Boolean {
        return opcode != Opcode.SIPUSH && classFile.getConstantPoolIndex(constant) == immediateShort
    }
}

/**
 * Describes an instruction followed by an immediate unsigned short.
 */
class SimpleImmediateShortInstruction(opcode: Opcode, immediateShort: Int = 0) : ImmediateShortInstruction(opcode, immediateShort)
