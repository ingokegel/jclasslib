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
 * Describes an entry of type VerificationType.UNINITIALIZED in a StackMapFrameEntry attribute structure.
 */
class UninitializedVerificationTypeInfoEntry : VerificationTypeInfoEntry(VerificationType.UNINITIALIZED) {

    /**
     * The offset.
     */
    var offset: Int = 0

    override fun readData(input: DataInput) {
        super.readData(input)
        offset = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        super.writeData(output)
        output.writeShort(offset)
    }

    override fun appendTo(buffer: StringBuilder) {
        super.appendTo(buffer)
        buffer.append(" (offset: ").append(offset).append(")")
    }

    override val length: Int
        get() = super.length + 2
}
