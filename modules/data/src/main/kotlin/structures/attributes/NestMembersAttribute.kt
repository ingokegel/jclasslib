/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.emptyArraySingleton
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes a NestMembers attribute structure.
 */
class NestMembersAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * List of class entries in the NestMembersAttribute structure
     */
    var entries: Array<NestMembersEntry> = emptyArraySingleton()

    override fun readData(input: DataInput) {

        val numberOfEntries = input.readUnsignedShort()
        entries = Array(numberOfEntries) {
            NestMembersEntry().apply {
                read(input)
            }
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(entries.size)
        entries.forEach { it.write(output) }
    }

    override fun getAttributeLength(): Int = 2 + entries.sumBy { it.length }

    override val debugInfo: String
        get() = "with ${entries.size} entries"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "NestMembers"

    }
}
