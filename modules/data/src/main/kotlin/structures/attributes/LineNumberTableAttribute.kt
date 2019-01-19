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
 * Describes an LineNumberTable attribute structure.
 */
class LineNumberTableAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Line number associations of the parent
     */
    var lineNumberTable: Array<LineNumberTableEntry> = emptyArraySingleton()

    override fun readData(input: DataInput) {
        val lineNumberTableLength = input.readUnsignedShort()
        lineNumberTable = Array(lineNumberTableLength) {
            LineNumberTableEntry().apply {
                read(input)
            }
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(lineNumberTable.size)
        lineNumberTable.forEach { it.write(output) }
    }

    override fun getAttributeLength(): Int = 2 + lineNumberTable.sumBy { it.length }

    override val debugInfo: String
        get() = "with ${lineNumberTable.size} entries"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "LineNumberTable"
    }
}
