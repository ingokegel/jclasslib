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
 * Describes an entry in a StackMapFrameEntry attribute structure.
 */
abstract class VerificationTypeInfoEntry(
        /**
         * The verification type
         */
        val type: VerificationType
) : AbstractStructure() {


    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(type.tag)
    }

    override fun read(input: DataInput) {

    }

    override val debugMessage: String
        get() = "VerificationTypeInfo entry of type $type"

    /**
     * Returns the bytecode length of the entry.
     */
    open val length: Int
        get() = 1

    /**
     * Append the verbose representation to a string builder.
     */
    open fun appendTo(buffer: StringBuilder) {
        buffer.append(type)
    }

    companion object {
        /**
         * Factory method for creating VerificationTypeInfoEntry structures.
         * @param input the DataInput from which to read the VerificationTypeInfoEntry structure
         * @param classFile the parent class file of the structure to be created
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        fun create(input: DataInput, classFile: ClassFile): VerificationTypeInfoEntry {
            val tag = input.readUnsignedByte()
            val verificationType = VerificationType.getFromTag(tag)
            return verificationType.createEntry().apply {
                this.classFile = classFile
                this.read(input)
            }
        }
    }


}
