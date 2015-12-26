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
 * Describes an entry of type VerificationType.OBJECT in a StackMapFrameEntry attribute structure.
 */
class ObjectVerificationTypeEntry : VerificationTypeInfoEntry(VerificationType.OBJECT) {

    var cpIndex: Int = 0

    override fun read(input: DataInput) {
        super.read(input)
        cpIndex = input.readUnsignedShort()
        if (isDebug) debug("read")
    }

    override fun write(output: DataOutput) {
        super.write(output)
        output.writeShort(cpIndex)
        if (isDebug) debug("wrote")
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
