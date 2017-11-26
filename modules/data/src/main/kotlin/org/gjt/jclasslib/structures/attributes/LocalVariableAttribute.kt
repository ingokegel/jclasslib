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
 * Contains common attributes to a local variable table attribute structure.
 */
abstract class LocalVariableAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Local variable associations of the parent code attribute
     */
    var localVariableEntries: Array<LocalVariableEntry> = emptyArraySingleton()

    override fun readData(input: DataInput) {
        val localVariableTableLength = input.readUnsignedShort()
        localVariableEntries = Array(localVariableTableLength) {
            LocalVariableEntry().apply {
                read(input)
            }
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(localVariableEntries.size)
        localVariableEntries.forEach { it.write(output) }
    }

    override val debugInfo: String
        get() = "with ${localVariableEntries.size} entries"

    override fun getAttributeLength(): Int = 2 + localVariableEntries.sumBy { it.length }

}
