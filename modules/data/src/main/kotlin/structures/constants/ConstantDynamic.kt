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
 * Base class for constants that reference a bootstrap method.
 */
abstract class ConstantDynamic(classFile: ClassFile) : AbstractConstant(classFile) {

    /**
     * The constant pool index of the bootstrap method attribute.
     */
    var bootstrapMethodAttributeIndex: Int = 0

    /**
     * The constant pool index of the associated [ConstantNameAndTypeInfo].
     */
    var nameAndTypeIndex: Int = 0

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = "${nameAndTypeInfo.name}, BootstrapMethods #$bootstrapMethodAttributeIndex"

    /**
     * The associated [ConstantNameAndTypeInfo] constant pool entry.
     */
    val nameAndTypeInfo: ConstantNameAndTypeInfo
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolEntry(nameAndTypeIndex, ConstantNameAndTypeInfo::class.java)

    override fun readData(input: DataInput) {
        bootstrapMethodAttributeIndex = input.readUnsignedShort()
        nameAndTypeIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(constantType.tag)
        output.writeShort(bootstrapMethodAttributeIndex)
        output.writeShort(nameAndTypeIndex)
    }

    override val debugInfo: String
        get() = "with bootstrapMethodAttributeIndex $bootstrapMethodAttributeIndex and nameAndTypeIndex $nameAndTypeIndex"

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantDynamic || other.constantType != constantType) {
            return false
        }
        return super.equals(other) && other.bootstrapMethodAttributeIndex == bootstrapMethodAttributeIndex && other.nameAndTypeIndex == nameAndTypeIndex
    }

    override fun hashCode(): Int = super.hashCode() xor bootstrapMethodAttributeIndex xor nameAndTypeIndex

}
