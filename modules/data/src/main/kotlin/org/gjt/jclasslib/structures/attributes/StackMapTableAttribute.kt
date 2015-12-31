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
 * Describes a BootstrapMethods attribute structure.
 */
class StackMapTableAttribute : AttributeInfo() {

    /**
     * stackMapFrame entries in the StackMapTableAttribute structure
     */
    var entries: Array<StackMapFrameEntry> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        val numberOfEntries = input.readUnsignedShort()
        var previousOffset = 0
        entries = Array(numberOfEntries) {
            StackMapFrameEntry.create(input, classFile, previousOffset).apply {
                previousOffset += offsetDelta + 1
            }
        }

        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(entries.size)
        entries.forEach { it.write(output) }

        debugWrite()
    }

    override fun getAttributeLength(): Int = 2 + entries.sumBy { it.length }

    override val debugInfo: String
        get() = "with ${entries.size} entries"

    companion object {

        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        val ATTRIBUTE_NAME = "StackMapTable"
    }
}
