/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import org.gjt.jclasslib.io.readByteCode
import org.gjt.jclasslib.structures.*

/**
 * Describes a Code attribute structure.
 */
class CodeAttribute(classFile: ClassFile) : AttributeInfo(classFile), AttributeContainer {

    /**
     * Maximum stack depth of this code attribute.
     */
    var maxStack: Int = 0

    /**
     * Maximum number of local variables of this code attribute.
     */
    var maxLocals: Int = 0

    /**
     * The code of this attribute as an array of bytes.
     */
    var code: ByteArray = ByteArray(0)

    /**
     * Exception table of this code attribute.
     */
    var exceptionTable: Array<ExceptionTableEntry> = emptyArraySingleton()

    override var attributes: Array<AttributeInfo> = emptyArraySingleton()

    override fun readData(input: DataInput) {

        maxStack = input.readUnsignedShort()
        maxLocals = input.readUnsignedShort()
        val codeLength = input.readInt()
        code = input.readByteArray(codeLength)
        if (isDebug) debug("read code with ${code.size} bytes", input)

        readExceptionTable(input)
        readAttributes(input, classFile)
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(maxStack)
        output.writeShort(maxLocals)
        output.writeInt(code.size)
        output.write(code)
        if (isDebug) debug("wrote code with ${code.size} bytes", output)

        writeExceptionTable(output)
        writeAttributes(output)
    }

    override fun isConstantUsed(constant: Constant, classFile: ClassFile): Boolean {
        return super.isConstantUsed(constant, classFile) ||
                readByteCode(code).any { it.isConstantUsed(constant, classFile) }
    }

    private fun readExceptionTable(input: DataInput) {
        val exceptionTableLength = input.readUnsignedShort()
        exceptionTable = Array(exceptionTableLength) {
            ExceptionTableEntry().apply {
                read(input)
            }
        }
        if (isDebug) debug("read exception table with ${exceptionTable.size} entries", input)
    }

    private fun writeExceptionTable(output: DataOutput) {
        output.writeShort(exceptionTable.size)
        exceptionTable.forEach { it.write(output) }
        if (isDebug) debug("wrote exception table with ${exceptionTable.size} entries", output)
    }

    override fun getAttributeLength(): Int = 12 + code.size +
            exceptionTable.sumOf { it.length } +
            totalAttributesLength

    override val debugInfo: String
        get() = "with maxStack $maxStack, maxLocals $maxLocals, code length ${code.size}"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "Code"
    }
}
