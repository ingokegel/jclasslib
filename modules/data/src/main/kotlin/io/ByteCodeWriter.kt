/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io

import org.gjt.jclasslib.bytecode.Instruction

import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Converts a list of instructions as defined in the package
 * org.gjt.jclasslib.code to code.
 */
object ByteCodeWriter {

    /**
     * Converts a list of instructions to code.
     * @param instructions the list of instructions
     * @return the code as an array of bytes
     */
    @Throws(IOException::class)
    @JvmStatic
    fun writeByteCode(instructions: List<Instruction>): ByteArray {

        val result = ByteArrayOutputStream()
        ByteCodeOutputStream(result).use {
            for (instruction in instructions) {
                writeNextInstruction(it, instruction)
            }
        }
        return result.toByteArray()
    }

    @Throws(IOException::class)
    private fun writeNextInstruction(output: ByteCodeOutputStream, instruction: Instruction) {
        instruction.write(output)
    }

}
