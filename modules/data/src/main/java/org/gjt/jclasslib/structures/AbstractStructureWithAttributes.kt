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
 * Base class for all structures with attributes.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
abstract class AbstractStructureWithAttributes : AbstractStructure() {

    /**
     * Attributes of this structure.
     */
    var attributes: Array<AttributeInfo> = emptyArray()

    /**
     * Find an attribute of a certain class.
     * @param attributeClass the class of the attribute
     * @return the found attribute, null if not found
     */
    fun findAttribute(attributeClass: Class<Any>): AttributeInfo? =
            attributes.firstOrNull {it.javaClass == attributeClass}

    /**
     * Read the attributes of this structure from the given DataInput.
     * @param input the DataInput from which to read
     */
    @Throws(InvalidByteCodeException::class, IOException::class)
    protected open fun readAttributes(input: DataInput) {

        val attributesCount = input.readUnsignedShort()
        attributes = Array<AttributeInfo>(attributesCount) {
            AttributeInfo.createOrSkip(input, classFile)
        }
    }

    /**
     * Write the attributes of this structure to the given DataOutput.
     * @param output the DataOutput to which to write
     */
    @Throws(InvalidByteCodeException::class, IOException::class)
    protected open fun writeAttributes(output: DataOutput) {

        val attributesCount = attributes.size
        output.writeShort(attributesCount)

        attributes.forEach { it.write(output) }
    }

    /**
     * Get the length of all attributes as a number of bytes.
     */
    protected val totalAttributesLength: Int
        get() = attributes.sumBy { it.attributeLength }
}
