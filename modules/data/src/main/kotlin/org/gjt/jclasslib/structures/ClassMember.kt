/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

import java.io.DataInput
import java.io.DataOutput

/**
 * Base class for class members.
 */
abstract class ClassMember(protected val classFile: ClassFile) : Structure(), AttributeContainer {

    /**
     * Access flags of this class member.
     */
    var accessFlags: Int = 0

    /**
     * The constant pool index of the name of this class member.
     */
    var nameIndex: Int = 0

    /**
     * The constant pool index of the descriptor of this class member.
     */
    var descriptorIndex: Int = 0

    override var attributes: Array<AttributeInfo> = emptyArray()

    /**
     * Get the Name of the class member.
     */
    val name: String
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolUtf8Entry(nameIndex).string

    /**
     * Verbose descriptor of the class member.
     */
    val descriptor: String
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolUtf8Entry(descriptorIndex).string

    /**
     * Access flags of this class as a hex string.
     */
    val formattedAccessFlags: String
        get() = printAccessFlags(accessFlags)

    /**
     * Verbose description of the access flags of this class.
     */
    val accessFlagsVerbose: String
        get() = printAccessFlagsVerbose(accessFlags)

    protected abstract fun printAccessFlagsVerbose(accessFlags: Int): String

    override fun readData(input: DataInput) {
        accessFlags = input.readUnsignedShort()
        nameIndex = input.readUnsignedShort()
        descriptorIndex = input.readUnsignedShort()

        readAttributes(input, classFile)
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(accessFlags)
        output.writeShort(nameIndex)
        output.writeShort(descriptorIndex)

        writeAttributes(output)
    }


    override val debugInfo: String
        get() = "with accessFlags $accessFlagsVerbose, nameIndex $nameIndex, descriptorIndex $descriptorIndex, ${attributes.size} attributes"

}
