/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.AbstractConstant
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.ConstantType
import org.gjt.jclasslib.structures.InvalidByteCodeException
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes a CONSTANT_Utf8_info constant pool data structure.
 */
class ConstantUtf8Info(classFile: ClassFile) : AbstractConstant(classFile) {

    /**
     * The string in this entry.
     */
    var string: String = ""

    override val constantType: ConstantType
        get() = ConstantType.UTF8

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = string

    /**
     * The byte array of the string in this entry.
     * *
     */
    val bytes: ByteArray
        get() = string.toByteArray()

    override fun readData(input: DataInput) {
        string = input.readUTF()
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(ConstantType.UTF8.tag)
        output.writeUTF(string)
    }

    override val debugInfo: String
        get() = "with length ${string.length} (\"$string\")"

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantUtf8Info) {
            return false
        }
        return super.equals(other) && other.string == string
    }

    override fun hashCode(): Int = super.hashCode() xor string.hashCode()

}
