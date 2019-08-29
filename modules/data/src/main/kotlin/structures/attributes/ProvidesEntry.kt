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
 * Describes a "provides" statement in a Module attribute structure.
 */
class ProvidesEntry : SubStructure() {

    /**
     * The index of the CONSTANT_Class_info structure that is provided by this module.
     */
    var index: Int = 0

    /**
     * Implementation classes as indices of CONSTANT_Class_info structures in the constant pool.
     */
    var withIndices: IntArray = IntArray(0)

    override fun readData(input: DataInput) {
        index = input.readUnsignedShort()
        val withCount = input.readUnsignedShort()
        withIndices = IntArray(withCount) {
            input.readUnsignedShort()
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(index)
        output.writeShort(withIndices.size)
        withIndices.forEach { output.writeShort(it) }
    }

    override val debugInfo: String
        get() = "with ${withIndices.size} implementation classes"

    /**
     * The length of the structure in bytes.
     */
    override val length: Int
        get() = 4 + 2 * withIndices.size

}
