/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

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
class UninitializedVerificationTypeEntry : VerificationTypeInfoEntry(VerificationType.UNINITIALIZED) {

    var offset: Int = 0

    override fun read(input: DataInput) {
        super.read(input)
        offset = input.readUnsignedShort()
        if (isDebug) debug("read")
    }

    override fun write(output: DataOutput) {
        super.write(output)
        output.writeShort(offset)
        if (isDebug) debug("wrote")
    }

    override fun appendTo(buffer: StringBuilder) {
        super.appendTo(buffer)
        buffer.append(" (offset: ").append(offset).append(")")
    }

    override val length: Int
        get() = super.length + 2
}
