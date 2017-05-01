/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import java.io.DataInput
import java.io.DataOutput

/**
 * Describes a hash entry in a ModuleHashes attribute structure.
 */
class HashEntry : SubStructure() {

    /**
     * The index of the CONSTANT_Module_info structure for which the hash has been computed.
     */
    var moduleNameIndex: Int = 0

    /**
     * Hash values
     */
    var hashValues: IntArray = IntArray(0)

    override fun readData(input: DataInput) {
        moduleNameIndex = input.readUnsignedShort()
        val hashValuesCount = input.readUnsignedShort()
        hashValues = IntArray(hashValuesCount) {
            input.readUnsignedByte()
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(moduleNameIndex)
        output.writeShort(hashValues.size)
        hashValues.forEach { output.writeByte(it) }
    }

    override val debugInfo: String
        get() = "with ${hashValues.size} hash values"

    /**
     * The length of the structure in bytes.
     */
    override val length: Int
        get() = 4 + hashValues.size

}
