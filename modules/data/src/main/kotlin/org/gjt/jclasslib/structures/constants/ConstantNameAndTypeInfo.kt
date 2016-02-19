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
 * Describes a CONSTANT_NameAndType_info constant pool data structure.
 */
class ConstantNameAndTypeInfo(classFile: ClassFile) : AbstractConstant(classFile) {

    /**
     * Index of the constant pool entry containing the name of this entry.
     */
    var nameIndex: Int = 0

    /**
     * Index of the constant pool entry containing the descriptor of this entry.
     */
    var descriptorIndex: Int = 0

    override val constantType: ConstantType
        get() = ConstantType.NAME_AND_TYPE

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = "$name : $descriptor"

    /**
     * The name of this entry.
     */
    val name: String
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolEntryName(nameIndex)

    /**
     * The descriptor string.
     */
    val descriptor: String
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolEntryName(descriptorIndex)

    override fun readData(input: DataInput) {
        nameIndex = input.readUnsignedShort()
        descriptorIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(ConstantType.NAME_AND_TYPE.tag)
        output.writeShort(nameIndex)
        output.writeShort(descriptorIndex)
    }

    override val debugInfo: String
        get() = "with nameIndex $nameIndex and descriptorIndex $descriptorIndex"

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantNameAndTypeInfo) {
            return false
        }
        return super.equals(other) && other.nameIndex == nameIndex && other.descriptorIndex == descriptorIndex
    }

    override fun hashCode(): Int = super.hashCode() xor nameIndex xor descriptorIndex

}
