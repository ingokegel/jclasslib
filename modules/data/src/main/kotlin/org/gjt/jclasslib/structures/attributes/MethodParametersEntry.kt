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
 * Describes an entry in a BootstrapMethods attribute structure.
 */
class MethodParametersEntry : AbstractStructure() {

    var nameIndex: Int = 0
    var accessFlags: Int = 0

    override fun readData(input: DataInput) {
        nameIndex = input.readUnsignedShort()
        accessFlags = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(nameIndex)
        output.writeShort(accessFlags)
    }

    override val debugInfo: String
        get() = ""

    val length: Int
        get() = 4

    companion object {
        /**
         * Factory method for creating StackMapFrameEntry structures.
         * @param input the DataInput from which to read the
         * @param classFile the parent class file of the structure to be created
         */
        fun create(input: DataInput, classFile: ClassFile) = MethodParametersEntry().apply {
            this.classFile = classFile
            this.read(input)
        }
    }

}
