/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import org.gjt.jclasslib.structures.AbstractConstant
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.ConstantType

/**
 * Describes a CONSTANT_MethodType_info constant pool data structure.
 */
class ConstantMethodTypeInfo(classFile: ClassFile) : AbstractConstant(classFile) {

    /**
     * Index of the constant pool entry containing the descriptor of the method.
     */
    var descriptorIndex: Int = 0

    override val constantType: ConstantType
        get() = ConstantType.METHOD_TYPE

    override val verbose: String
        get() = name

    /**
     * The descriptor.
     */
    val name: String
        get() = typeConstant.string

    val typeConstant: ConstantUtf8Info
        get() = classFile.getConstantPoolUtf8Entry(descriptorIndex)

    override fun readData(input: DataInput) {
        descriptorIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(ConstantType.METHOD_TYPE.tag)
        output.writeShort(descriptorIndex)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantMethodTypeInfo) {
            return false
        }
        return super.equals(other) && other.descriptorIndex == descriptorIndex
    }

    override fun hashCode(): Int = super.hashCode() xor descriptorIndex

    override val debugInfo: String
        get() = "with descriptorIndex $descriptorIndex"
}
