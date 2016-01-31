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
 * Describes an entry in a BootstrapMethods attribute structure.
 */
class MethodParametersEntry : SubStructure() {

    /**
     * The name index.
     */
    var nameIndex: Int = 0

    /**
     * The access flags.
     */
    var accessFlags: Int = 0

    /**
     * Verbose description of the access flags
     */
    val accessFlagsVerbose: String
        get() = formatAccessFlagsVerbose(AccessFlag.METHOD_PARAMETERS_ACCESS_FLAGS, accessFlags)

    override fun readData(input: DataInput) {
        nameIndex = input.readUnsignedShort()
        accessFlags = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(nameIndex)
        output.writeShort(accessFlags)
    }

    override val debugInfo: String
        get() = ""

    /**
     * The length of the structure in bytes.
     */
    override val length: Int
        get() = 4

}
