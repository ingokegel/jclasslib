/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AbstractStructure
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an entry in a LineNumberTable attribute structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com), [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class LineNumberTableEntry : AbstractStructure() {

    /**
     * start_pc of this line number association.
     */
    var startPc: Int = 0

    /**
     * Line number of this line number association.
     */
    var lineNumber: Int = 0

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        startPc = input.readUnsignedShort()
        lineNumber = input.readUnsignedShort()

        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(startPc)
        output.writeShort(lineNumber)

        if (isDebug) debug("wrote")
    }

    override fun debug(message: String) {
        super.debug("$message LineNumberTable entry with start_pc $startPc, line_number $lineNumber")
    }

    companion object {

        /**
         * Length in bytes of a line number association.
         */
        val LENGTH = 4

        /**
         * Factory method for creating LineNumberTableEntry structures.
         * @param input the DataInput from which to read the LineNumberTableEntry structure
         * @param classFile the parent class file of the structure to be created
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        fun create(input: DataInput, classFile: ClassFile) = LineNumberTableEntry().apply {
            this.classFile = classFile
            this.read(input)
        }
    }

}
