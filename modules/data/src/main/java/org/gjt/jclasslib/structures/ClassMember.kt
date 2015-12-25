/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Base class for class members.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
abstract class ClassMember : AbstractStructureWithAttributes() {

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

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {

        accessFlags = input.readUnsignedShort()
        nameIndex = input.readUnsignedShort()
        descriptorIndex = input.readUnsignedShort()

        readAttributes(input)

    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {

        output.writeShort(accessFlags)
        output.writeShort(nameIndex)
        output.writeShort(descriptorIndex)

        writeAttributes(output)
    }

}
