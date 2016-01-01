/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an LineNumberTable attribute structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class LineNumberTableAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Line number associations of the parent
     */
    var lineNumberTable: Array<LineNumberTableEntry> = emptyArray()

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

    override fun getAttributeLength(): Int {
        return 2 + lineNumberTable.size * LineNumberTableEntry.LENGTH
    }

    override val debugInfo: String
        get() = "with ${lineNumberTable.size} entries"

    companion object {

        /** Name of the attribute as in the corresponding constant pool entry.  */
        val ATTRIBUTE_NAME = "LineNumberTable"
    }
}
