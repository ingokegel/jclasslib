/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import java.io.DataInput
import java.io.DataOutput

/**
 * Target info for a TypeAnnotation structure with a bytecode offset.
 */
class OffsetTargetInfo : TargetInfo() {

    /**
     * The bytecode offset.
     */
    var offset: Int = 0

    override fun readData(input: DataInput) {
        offset = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(offset)
    }

    override val length: Int
        get() = 2

    override val verbose: String
        get() = "offset $offset"

    override val debugInfo: String
        get() = "with $verbose"
}
