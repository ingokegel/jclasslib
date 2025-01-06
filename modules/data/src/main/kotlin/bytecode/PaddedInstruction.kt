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
 * Base class for instructions which need four bytes padding relative
 * to the start of the enclosing code for the parent Code
 * attribute before reading immediate arguments.
 */
abstract class PaddedInstruction(opcode: Opcode) : Instruction(opcode) {

    /**
     * Get the padded size in bytes of this instruction.
     * @param offset the offset at which this instruction is found.
     * @return the padded size in bytes
     */
    override fun getPaddedSize(offset: Int): Int = size + paddingBytes(offset + 1)

    override fun read(input: CountingDataInput) {
        super.read(input)

        val bytesToRead = paddingBytes(input.bytesRead)
        repeat (bytesToRead) {
            input.readByte()
        }
    }

    override fun write(output: CountingDataOutput) {
        super.write(output)

        val bytesToWrite = paddingBytes(output.bytesWritten)
        repeat(bytesToWrite) {
            output.writeByte(0)
        }
    }

    private fun paddingBytes(bytesCount: Int): Int {
        val bytesToPad = 4 - bytesCount % 4
        return if (bytesToPad == 4) 0 else bytesToPad
    }
}
