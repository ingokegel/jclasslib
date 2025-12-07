/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.emptyArraySingleton

/**
 * Describes a ModuleHashes attribute structure.
 */
class ModuleHashesAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Constant pool index of the CONSTANT_Utf8_info structure containing the algorithm name.
     */
    var algorithmIndex: Int = 0
    /**
     * Array of the module hashes
     */
    var hashEntries: Array<HashEntry> = emptyArraySingleton()

    override fun readData(input: DataInput) {
        algorithmIndex = input.readUnsignedShort()
        val hashEntriesCount = input.readUnsignedShort()
        hashEntries = Array(hashEntriesCount) {
            HashEntry().apply {
                read(input)
            }
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(algorithmIndex)
        output.writeShort(hashEntries.size)
        hashEntries.forEach { it.write(output) }
    }

    override fun getUsedConstantPoolIndices() = intArrayOf(attributeNameIndex, algorithmIndex)

    override fun isConstantUsed(constant: Constant, classFile: ClassFile): Boolean {
        return super.isConstantUsed(constant, classFile) ||
                hashEntries.any { it.isConstantUsed(constant, classFile) }
    }

    override fun getAttributeLength(): Int =  4 + hashEntries.sumOf { it.length }

    override val debugInfo: String
        get() = "with ${hashEntries.size} hash entries"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "ModuleHashes"
    }
}