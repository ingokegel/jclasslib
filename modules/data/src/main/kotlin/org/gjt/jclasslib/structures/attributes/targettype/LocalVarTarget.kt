/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import org.gjt.jclasslib.structures.AbstractStructure
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Local variable target for the LocalVarTargetInfo structure.
 */
class LocalVarTarget : AbstractStructure() {

    var startPc: Int = 0
    var length: Int = 0
    var index: Int = 0

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        startPc = input.readUnsignedShort()
        length = input.readUnsignedShort()
        index = input.readUnsignedShort()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(startPc)
        output.writeShort(length)
        output.writeShort(index)
    }

    override val debugMessage: String
        get() = "LocalVarTarget with startPc $startPc, length $length, index $index"
}
