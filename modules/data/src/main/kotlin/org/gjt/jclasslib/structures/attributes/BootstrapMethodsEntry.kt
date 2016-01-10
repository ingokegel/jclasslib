/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFile
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an entry in a BootstrapMethods attribute structure.
 */
class BootstrapMethodsEntry(private val classFile: ClassFile) : SubStructure() {

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

    /**
     * Verbose representation of the bootstrap method entry.
     */
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

    /**
     * Length of the structure in bytes.
     */
    override val length: Int
        get() = 4 + argumentIndices.size * 2

}
