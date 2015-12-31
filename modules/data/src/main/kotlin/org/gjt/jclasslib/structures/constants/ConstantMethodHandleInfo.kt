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
 * Describes a CONSTANT_MethodHandle_info constant pool data structure.

 * @author [Hannes Kegel](mailto:jclasslib@ej-technologies.com)
 */
class ConstantMethodHandleInfo : CPInfo() {

    /**
     * Index of the constant pool entry containing the reference.
     */
    var referenceIndex: Int = 0

    /**
     * Method handle type.
     */
    lateinit var type: MethodHandleType

    override val constantType: ConstantType
        get() = ConstantType.METHOD_HANDLE

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = name

    /**
     * Get the descriptor.
     * @return the descriptor
     * @throws org.gjt.jclasslib.structures.InvalidByteCodeException if the byte code is invalid
     */
    val name: String
        @Throws(InvalidByteCodeException::class)
        get() = constantType.verbose + " " + classFile.getConstantPoolEntryName(referenceIndex)

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        type = MethodHandleType.getFromTag(input.readByte().toInt())
        referenceIndex = input.readUnsignedShort()
        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(ConstantType.METHOD_HANDLE.tag)
        output.write(type.tag)
        output.writeShort(referenceIndex)
        debugWrite()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantMethodHandleInfo) {
            return false
        }
        return super.equals(other) && other.referenceIndex == referenceIndex && other.type == type
    }

    override fun hashCode(): Int = super.hashCode() xor referenceIndex

    override val debugInfo: String
        get() = "with referenceIndex $referenceIndex and type $type"
}
