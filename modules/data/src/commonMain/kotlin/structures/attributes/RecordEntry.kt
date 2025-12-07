/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import org.gjt.jclasslib.structures.AttributeContainer
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile

/**
 * Describes an entry in a Record attribute structure.
 */
class RecordEntry(private val classFile: ClassFile, override var attributes: Array<AttributeInfo>) : SubStructure(), AttributeContainer {

    /**
     * The name index.
     */
    var nameIndex: Int = 0

    /**
     * The description index.
     */
    var descriptorIndex: Int = 0

    override fun readData(input: DataInput) {
        nameIndex = input.readUnsignedShort()
        descriptorIndex = input.readUnsignedShort()
        readAttributes(input, classFile)
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(nameIndex)
        output.writeShort(descriptorIndex)
        writeAttributes(output)
    }

    override fun getUsedConstantPoolIndices() = intArrayOf(nameIndex, descriptorIndex)

    override val debugInfo: String
        get() = ""

    /**
     * The length of the structure in bytes.
     */
    override val length: Int
        get() = 6 + totalAttributesLength

}
