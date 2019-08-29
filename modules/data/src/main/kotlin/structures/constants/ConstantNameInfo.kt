/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.AbstractConstant
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException
import java.io.DataInput
import java.io.DataOutput

/**
 * Base class for constant pool data structures that reference a name.
 */
abstract class ConstantNameInfo(classFile: ClassFile) : AbstractConstant(classFile) {

    /**
     * Index of the constant pool entry containing the referenced name.
     */
    var nameIndex: Int = 0

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = name

    /**
     * The referenced name.
     */
    val name: String
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolUtf8Entry(nameIndex).string

    override fun readData(input: DataInput) {
        nameIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(constantType.tag)
        output.writeShort(nameIndex)
    }

    override fun equals(other: Any?): Boolean {
        if (other?.javaClass != this.javaClass) {
            return false
        }
        return super.equals(other) && (other as ConstantNameInfo).nameIndex == nameIndex
    }

    override fun hashCode(): Int = super.hashCode() xor nameIndex

    override val debugInfo: String
        get() = "with nameIndex $nameIndex"
}
