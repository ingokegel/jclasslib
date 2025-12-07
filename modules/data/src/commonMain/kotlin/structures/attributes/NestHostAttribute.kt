/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.constants.ConstantClassInfo

/**
 * Describes a NestHost attribute structure.
 */
class NestHostAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Constant pool index of the CONSTANT_Class_info structure containing the host class.
     */
    var classInfoIndex: Int = 0

    /**
     * Returns the constant that is referenced by the [classInfoIndex] index.
     */
    val classConstant: ConstantClassInfo
        get() = classFile.getConstantPoolEntry(classInfoIndex, ConstantClassInfo::class)

    override fun readData(input: DataInput) {
        classInfoIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(classInfoIndex)
    }

    override fun getUsedConstantPoolIndices() = intArrayOf(attributeNameIndex, classInfoIndex)

    override fun getAttributeLength(): Int =  2

    override val debugInfo: String
        get() = ""

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "NestHost"
    }
}