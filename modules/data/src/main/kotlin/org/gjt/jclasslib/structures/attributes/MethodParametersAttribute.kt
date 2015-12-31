/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes a MethodParameter attribute structure.
 */
class MethodParametersAttribute : AttributeInfo() {

    /**
     * List of stackMapFrame entries in the StackMapTableAttribute structure
     */
    var entries: Array<MethodParametersEntry> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {

        val numberOfEntries = input.readUnsignedByte()
        entries = Array(numberOfEntries) {
            MethodParametersEntry.create(input, classFile)
        }

        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(entries.size)
        entries.forEach { it.write(output) }

        debugWrite()
    }

    override fun getAttributeLength(): Int = 1 + entries.sumBy { it.length }

    override val debugInfo: String
        get() = "with ${entries.size} entries"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        val ATTRIBUTE_NAME = "MethodParameters"

    }
}
