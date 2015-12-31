/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.CPInfo
import org.gjt.jclasslib.structures.ConstantType
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes a CONSTANT_Class_info constant pool data structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class ConstantClassInfo : CPInfo() {

    /**
     * Index of the constant pool entry containing the name of the class.
     */
    var nameIndex: Int = 0

    override val constantType: ConstantType
        get() = ConstantType.CLASS

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = name

    /**
     * Name of the class.
     */
    val name: String
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolUtf8Entry(nameIndex).string

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        nameIndex = input.readUnsignedShort()
        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(ConstantType.CLASS.tag)
        output.writeShort(nameIndex)
        debugWrite()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantClassInfo) {
            return false
        }
        return super.equals(other) && other.nameIndex == nameIndex
    }

    override fun hashCode(): Int = super.hashCode() xor nameIndex

    override val debugInfo: String
        get() = "with nameIndex $nameIndex"
}
