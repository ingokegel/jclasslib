/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import org.gjt.jclasslib.structures.attributes.SubStructure
import java.io.DataInput
import java.io.DataOutput

/**
 * Local variable target for the LocalVarTargetInfo structure.
 */
class LocalVarTarget : SubStructure() {

    /**
     * start_pc of this local variable target.
     */
    var startPc: Int = 0

    /**
     * Length in bytes of this local variable target.
     */
    var targetLength: Int = 0

    /**
     * Index of this local variable target.
     */
    var index: Int = 0

    override fun readData(input: DataInput) {
        startPc = input.readUnsignedShort()
        targetLength = input.readUnsignedShort()
        index = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(startPc)
        output.writeShort(targetLength)
        output.writeShort(index)
    }

    override val debugInfo: String
        get() = "with startPc $startPc, length $targetLength, index $index"

    override val length: Int
        get() = 6
}
