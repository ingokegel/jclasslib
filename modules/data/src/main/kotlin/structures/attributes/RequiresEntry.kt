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
 * Describes a "requires" statement in a Module attribute structure.
 */
class RequiresEntry : SubStructure() {

    /**
     * The index of the CONSTANT_Module_info structure that is required.
     */
    var index: Int = 0

    /**
     * The flags of the requires statement.
     */
    var flags: Int = 0

    /**
     * Constant pool index of the CONSTANT_Utf8_info structure containing the required module version.
     * Contains 0 if no information is available.
     */
    var versionIndex: Int = 0

    /**
     * Verbose description of the access flags
     */
    val flagsVerbose: String
        get() = formatAccessFlagsVerbose(AccessFlag.REQUIRES_FLAGS, flags)

    override fun readData(input: DataInput) {
        index = input.readUnsignedShort()
        flags = input.readUnsignedShort()
        versionIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(index)
        output.writeShort(flags)
        output.writeShort(versionIndex)
    }

    override val debugInfo: String
        get() = ""

    /**
     * The length of the structure in bytes.
     */
    override val length: Int
        get() = 6

}
