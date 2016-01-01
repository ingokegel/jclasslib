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

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
open class Instruction(
        /**
         * Opcode of this instruction.
         */
        val opcode: Opcode
) {

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
     * Read this instruction from the given ByteCodeInput.
     *
     *

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
