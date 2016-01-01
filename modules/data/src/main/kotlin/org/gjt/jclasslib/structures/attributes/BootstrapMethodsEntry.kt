/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AbstractStructure
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an entry in a BootstrapMethods attribute structure.
 */
class BootstrapMethodsEntry : AbstractStructure() {

    /**
     * Constant pool index of the CONSTANT_MethodRef_info structure
     * describing the bootstrap method of this BootstrapMethodsEntry.
     */
    var methodRefIndex: Int = 0

    /**
     * Set the array of argument references of this BootstrapMethodsEntry.
     */
    var argumentIndices: IntArray = IntArray(0)

    override fun readData(input: DataInput) {
        methodRefIndex = input.readUnsignedShort()
        val argumentRefsCount = input.readUnsignedShort()
        argumentIndices = IntArray(argumentRefsCount) {
            input.readUnsignedShort()
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(methodRefIndex)
        output.writeShort(argumentIndices.size)
        argumentIndices.forEach { output.writeShort(it) }
    }

    override val debugInfo: String
        get() = "with methodRefIndex $methodRefIndex, arguments ($verbose)"

    val verbose: String
        get() {
            val buffer = StringBuilder()
            argumentIndices.forEachIndexed { i, argumentIndex ->
                if (i > 0) {
                    buffer.append("\n")
                }
                buffer.append("<a href=\"").append(argumentIndex).append("\">cp_info #").append(argumentIndex).append("</a> &lt;").append(getVerboseIndex(argumentIndex)).append("&gt;")
            }
            return buffer.toString()
        }

    private fun getVerboseIndex(index: Int): String = classFile.getConstantPoolEntryName(index)

    val length: Int
        get() = 4 + argumentIndices.size * 2

    companion object {
        /**
         * Factory method for creating BootstrapMethodsEntry structures.
         * @param input the DataInput from which to read the BootstrapMethodsEntry structure
         * @param classFile the parent class file of the structure to be created
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        fun create(input: DataInput, classFile: ClassFile) = BootstrapMethodsEntry().apply {
            this.classFile = classFile
            this.read(input)
        }
    }

}
