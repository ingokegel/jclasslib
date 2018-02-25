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
 * Base class for all opcode instruction wrappers.
 * @property opcode Opcode of this instruction.
 */
open class Instruction(val opcode: Opcode) {

    /**
     * Offset of this instruction in its parent Code attribute.
     */
    var offset: Int = 0

    /**
     * Size in bytes of this instruction.
     */
    open val size: Int
        get() = 1

    /**
     * Get the padded size in bytes of this instruction. This will be the same as
     * `size` except for instances of [PaddedInstruction].
     * @param offset the offset at which this instruction is found.
     * @return the padded size in bytes
     */
    open fun getPaddedSize(offset: Int): Int = size

    /**
     * Read this instruction from the given ByteCodeInput.
     * Expects ByteCodeInput to be in JVM class file format and just
     * before a instruction of this kind.
     * @param input the ByteCodeInput from which to read
     */
    open fun read(input: ByteCodeInput) {
        // The opcode has already been read
        offset = input.bytesRead - 1
    }

    /**
     * Write this instruction to the given ByteCodeOutput.
     * @param output the ByteCodeOutput to which to write
     */
    open fun write(output: ByteCodeOutput) {
        output.writeByte(opcode.tag)
    }

}
