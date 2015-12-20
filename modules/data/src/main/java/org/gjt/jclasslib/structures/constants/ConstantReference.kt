/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.CPInfo
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Base class for constant pool data structures which reference class members.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
abstract class ConstantReference : CPInfo() {

    /**
     * Index of the constant pool entry containing the
     * CONSTANT_Class_info of this entry.
     */
    var classIndex: Int = 0

    /**
     * Index of the constant pool entry containing the
     * CONSTANT_NameAndType_info of this entry.
     */
    var nameAndTypeIndex: Int = 0

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolEntryName(classIndex) + "." + classFile.getConstantPoolEntryName(nameAndTypeInfo.nameIndex)

    /**
     * Class info for this reference.
     */
    val classInfo: ConstantClassInfo
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolEntry(classIndex, ConstantClassInfo::class.java)

    /**
     * Name and type info for this reference.
     * @throws InvalidByteCodeException
     */
    val nameAndTypeInfo: ConstantNameAndTypeInfo
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolEntry(nameAndTypeIndex, ConstantNameAndTypeInfo::class.java)

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        classIndex = input.readUnsignedShort()
        nameAndTypeIndex = input.readUnsignedShort()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(classIndex)
        output.writeShort(nameAndTypeIndex)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantReference) {
            return false
        }
        return super.equals(other) && other.classIndex == classIndex && other.nameAndTypeIndex == nameAndTypeIndex
    }

    override fun hashCode(): Int = super.hashCode() xor classIndex xor nameAndTypeIndex

}
