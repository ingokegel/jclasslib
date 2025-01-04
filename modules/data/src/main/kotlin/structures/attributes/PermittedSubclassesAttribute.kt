/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.emptyArraySingleton
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes a PermittedSubclasses attribute structure.
 */
class PermittedSubclassesAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * List of classes in the PermittedSubclassesAttribute structure
     */
    var entries: Array<Int> = emptyArraySingleton()

    override fun readData(input: DataInput) {

        val numberOfEntries = input.readUnsignedShort()
        entries = Array(numberOfEntries) {
            input.readUnsignedShort()
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(entries.size)
        entries.forEach { output.writeShort(it) }
    }

    override fun getAttributeLength(): Int = 2 + 2 * entries.size

    override val debugInfo: String
        get() = "with ${entries.size} entries"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "PermittedSubclasses"

    }
}
