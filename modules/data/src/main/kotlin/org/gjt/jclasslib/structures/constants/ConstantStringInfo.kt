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
 * Describes a CONSTANT_String_info constant pool data structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class ConstantStringInfo : CPInfo() {

    /**
     * Index of the constant pool entry containing the
     * string of this entry.
     */
    var stringIndex: Int = 0

    override val constantType: ConstantType
        get() = ConstantType.STRING

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolEntryName(stringIndex)

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        stringIndex = input.readUnsignedShort()
        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(ConstantType.STRING.tag)
        output.writeShort(stringIndex)
        debugWrite()
    }

    override val debugInfo: String
        get() = "with stringIndex $stringIndex"

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantStringInfo) {
            return false
        }
        return super.equals(other) && other.stringIndex == stringIndex
    }

    override fun hashCode(): Int = super.hashCode() xor stringIndex

}
