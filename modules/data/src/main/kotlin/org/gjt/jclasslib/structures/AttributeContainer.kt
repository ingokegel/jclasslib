/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures

import org.gjt.jclasslib.io.Log
import java.io.DataInput
import java.io.DataOutput

/**
 * Read the attributes of this structure from the given DataInput.
 * @param input the DataInput from which to read
 */
fun AttributeContainer.readAttributes(input: DataInput, classFile: ClassFile) {
    val attributesCount = input.readUnsignedShort()
    if (java.lang.Boolean.getBoolean(AttributeInfo.SYSTEM_PROPERTY_SKIP_ATTRIBUTES)) {
        input.skipBytes(2)
        input.skipBytes(input.readInt())
    } else {
        this.attributes = Array(attributesCount) {
            AttributeInfo.create(input, classFile)
        }
        if (Structure.isDebug) Log.debug("read $attributesCount attributes")
    }
}

/**
 * Write the attributes of this structure to the given DataOutput.
 * @param output the DataOutput to which to write
 */
fun AttributeContainer.writeAttributes(output: DataOutput) {
    val attributesCount = this.attributes.size
    output.writeShort(attributesCount)
    this.attributes.forEach { it.write(output) }
    if (Structure.isDebug) Log.debug("wrote $attributesCount attributes")
}

interface AttributeContainer {

    /**
     * Attributes of this structure.
     */
    var attributes: Array<AttributeInfo>

    /**
     * Find an attribute of a certain class.
     * @param attributeClass the class of the attribute
     * @return the found attribute, null if not found
     */
    fun findAttribute(attributeClass: Class<Any>): AttributeInfo? =
            attributes.firstOrNull {it.javaClass == attributeClass}

    /**
     * Get the length of all attributes as a number of bytes.
     */
    val totalAttributesLength: Int
        get() = attributes.sumBy { it.getAttributeLength() }
}