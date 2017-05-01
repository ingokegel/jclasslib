/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AccessFlag
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes a "exports" or "opens" statement in a Module attribute structure.
 */
class ExportsEntry : SubStructure() {

    /**
     * The index of the CONSTANT_Module_info structure that is exported or opened.
     */
    var index: Int = 0

    /**
     * The flags of the export statement.
     */
    var flags: Int = 0

    /**
     * Modules to which the exports or opens statement applies.
     * Empty array if exported to all modules.
     */
    var toIndices: IntArray = IntArray(0)

    /**
     * Verbose description of the access flags
     */
    val flagsVerbose: String
        get() = formatAccessFlagsVerbose(AccessFlag.EXPORTS_FLAGS, flags)

    override fun readData(input: DataInput) {
        index = input.readUnsignedShort()
        flags = input.readUnsignedShort()
        val toCount = input.readUnsignedShort()
        toIndices = IntArray(toCount) {
            input.readUnsignedShort()
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(index)
        output.writeShort(flags)
        output.writeShort(toIndices.size)
        toIndices.forEach { output.writeShort(it) }
    }

    override val debugInfo: String
        get() = "with ${toIndices.size} target modules"

    /**
     * The length of the structure in bytes.
     */
    override val length: Int
        get() = 6 + 2 * toIndices.size

}
