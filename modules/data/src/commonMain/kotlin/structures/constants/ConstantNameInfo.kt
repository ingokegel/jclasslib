/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

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

/**
 * Base class for constant pool data structures that reference a name.
 */
abstract class ConstantNameInfo(classFile: ClassFile) : AbstractConstant(classFile) {

    /**
     * Index of the constant pool entry containing the referenced name.
     */
    var nameIndex: Int = 0

    /**
     * Returns the ConstantUtf8Info constant pool entry that contains the actual string.
     */
    val nameConstant: ConstantUtf8Info
        get() = classFile.getConstantPoolUtf8Entry(nameIndex)

    override val verbose: String
        get() = name

    /**
     * The referenced name.
     */
    val name: String
        get() = nameConstant.string

    override fun readData(input: DataInput) {
        nameIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(constantType.tag)
        output.writeShort(nameIndex)
    }

    override fun getUsedConstantPoolIndices() = intArrayOf(nameIndex)

    override fun equals(other: Any?): Boolean {
        if (other != null && other::class != this::class) {
            return false
        }
        return super.equals(other) && (other as ConstantNameInfo).nameIndex == nameIndex
    }

    override fun hashCode(): Int = super.hashCode() xor nameIndex

    override val debugInfo: String
        get() = "with nameIndex $nameIndex"
}
