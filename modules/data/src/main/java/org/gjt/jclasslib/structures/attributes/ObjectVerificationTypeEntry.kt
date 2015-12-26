/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an entry of type VerificationType.OBJECT in a StackMapFrameEntry attribute structure.
 */
class ObjectVerificationTypeEntry : VerificationTypeInfoEntry(VerificationType.OBJECT) {

    var cpIndex: Int = 0

    @Throws(InvalidByteCodeException::class, IOException::class)
    public override fun readExtra(input: DataInput) {
        super.readExtra(input)
        cpIndex = input.readUnsignedShort()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    public override fun writeExtra(output: DataOutput) {
        super.writeExtra(output)
        output.writeShort(cpIndex)
    }

    override fun appendTo(buffer: StringBuilder) {
        super.appendTo(buffer)
        buffer.append(" <a href=\"").append(cpIndex).append("\">cp_info #").append(cpIndex).append("</a> &lt;").append(verboseIndex).append("&gt;")
    }

    private val verboseIndex: String
        get() = classFile.getConstantPoolEntryName(cpIndex)

    override val length: Int
        get() = super.length + 2
}
