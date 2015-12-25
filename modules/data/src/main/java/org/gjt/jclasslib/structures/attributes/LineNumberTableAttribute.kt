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
 * Describes an LineNumberTable attribute structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class LineNumberTableAttribute : AttributeInfo() {

    /**
     * Line number associations of the parent
     */
    var lineNumberTable: Array<LineNumberTableEntry> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        val lineNumberTableLength = input.readUnsignedShort()
        lineNumberTable = Array(lineNumberTableLength) {
            LineNumberTableEntry.create(input, classFile)
        }

        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        lineNumberTable.forEach { it.write(output) }

        if (isDebug) debug("wrote")
    }

    override fun getAttributeLength(): Int {
        return 2 + lineNumberTable.size * LineNumberTableEntry.LENGTH
    }

    override fun debug(message: String) {
        super.debug("$message LineNumberTable attribute with ${lineNumberTable.size} entries")
    }

    companion object {

        /** Name of the attribute as in the corresponding constant pool entry.  */
        val ATTRIBUTE_NAME = "LineNumberTable"
    }
}
