/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info

/**
 * Base class for class members.
 * @property classFile The class file that this class member is a part of.
 */
abstract class ClassMember(protected val classFile: ClassFile) : Structure(), AttributeContainer {

    /**
     * Access flags of this class member.
     */
    var accessFlags: Int = 0

    /**
     * The constant pool index for the name of this class member.
     */
    var nameIndex: Int = 0

    /**
     * Returns the constant that is referenced by the [nameIndex] index.
     */
    val nameConstant: ConstantUtf8Info
        get() = classFile.getConstantPoolUtf8Entry(nameIndex)

    /**
     * The constant pool index for the descriptor of this class member.
     */
    var descriptorIndex: Int = 0

    /**
     * Returns the constant that is referenced by the [descriptorIndex] index.
     */
    val descriptorConstant: ConstantUtf8Info
        get() = classFile.getConstantPoolUtf8Entry(descriptorIndex)

    override var attributes: Array<AttributeInfo> = emptyArraySingleton()

    /**
     * Name of the class member.
     */
    val name: String
        get() = classFile.getConstantPoolUtf8Entry(nameIndex).string

    /**
     * Verbose descriptor of the class member.
     */
    val descriptor: String
        get() = classFile.getConstantPoolUtf8Entry(descriptorIndex).string

    /**
     * Access flags of this class as a hex string.
     */
    val formattedAccessFlags: String
        get() = formatFlags(accessFlags)

    /**
     * Verbose description for the access flags of this class.
     */
    val accessFlagsVerbose: String
        get() = formatAccessFlagsVerbose(accessFlags)

    /**
     * Verbose description of the specified access flags.
     * @param accessFlags the access flags
     */
    protected abstract fun formatAccessFlagsVerbose(accessFlags: Int): String

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
