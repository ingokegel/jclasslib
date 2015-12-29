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
 * Describes an Exceptions attribute structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class ExceptionsAttribute : AttributeInfo() {

    /**
     * Exceptions thrown by the parent code attribute
     */
    var exceptionIndexTable: IntArray = IntArray(0)

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        val numberOfExceptions = input.readUnsignedShort()
        exceptionIndexTable = IntArray(numberOfExceptions) {
            input.readUnsignedShort()
        }

        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(exceptionIndexTable.size)
        exceptionIndexTable.forEach { output.writeShort(it) }

        if (isDebug) debug("wrote")
    }

    override fun getAttributeLength(): Int = 2 + 2 * exceptionIndexTable.size

    override fun debug(message: String) {
        super.debug("$message Exception attribute with ${exceptionIndexTable.size} exceptions")
    }

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        val ATTRIBUTE_NAME = "Exceptions"
    }
}
