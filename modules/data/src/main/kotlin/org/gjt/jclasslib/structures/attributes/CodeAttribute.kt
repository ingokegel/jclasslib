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
 * Describes a Code attribute structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class CodeAttribute : AttributeInfo() {

    /**
     * Maximum stack depth of this code attribute.
     */
    var maxStack: Int = 0

    /**
     * Maximum number of local variables of this code attribute.
     */
    var maxLocals: Int = 0

    /**
     * Code of this code attribute as an array of bytes.
     */
    var code: ByteArray = ByteArray(0)

    /**
     * Exception table of this code attribute.
     */
    var exceptionTable: Array<ExceptionTableEntry> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {

        maxStack = input.readUnsignedShort()
        maxLocals = input.readUnsignedShort()
        val codeLength = input.readInt()
        code = ByteArray(codeLength)
        input.readFully(code)

        readExceptionTable(input)
        readAttributes(input)

        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(maxStack)
        output.writeShort(maxLocals)
        output.writeInt(code.size)
        output.write(code)

        writeExceptionTable(output)
        writeAttributes(output)

        debugWrite()
    }

    private fun readExceptionTable(input: DataInput) {

        val exceptionTableLength = input.readUnsignedShort()
        exceptionTable = Array(exceptionTableLength) {
            ExceptionTableEntry.create(input, classFile)
        }
    }

    private fun writeExceptionTable(output: DataOutput) {
        output.writeShort(exceptionTable.size)
        exceptionTable.forEach { it.write(output) }
    }

    override fun getAttributeLength(): Int = 12 + code.size +
            exceptionTable.size * ExceptionTableEntry.LENGTH +
            6 * attributes.size +
            totalAttributesLength

    override val debugInfo: String
        get() = "with maxStack $maxStack, maxLocals $maxLocals, code length ${code.size}"

    companion object {
        /** Name of the attribute as in the corresponding constant pool entry.  */
        val ATTRIBUTE_NAME = "Code"
    }
}
