/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFile
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an entry of type [VerificationType.OBJECT] in a StackMapFrameEntry attribute structure.
 */
class ObjectVerificationTypeInfoEntry(private val classFile: ClassFile) : VerificationTypeInfoEntry(VerificationType.OBJECT) {

    /**
     * The constant pool index of the associated ConstantClassInfo.
     */
    var cpIndex: Int = 0

    override fun readData(input: DataInput) {
        super.readData(input)
        cpIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        super.writeData(output)
        output.writeShort(cpIndex)
    }

    override fun appendTo(buffer: StringBuilder) {
        super.appendTo(buffer)
        buffer.append(" <a href=\"").append(cpIndex).append("\">cp_info #").append(cpIndex).append("</a> &lt;").append(verboseIndex).append("&gt;")
    }

    private val verboseIndex: String
        get() = classFile.getConstantPoolEntryName(cpIndex)

    /**
     * The length of the structure in bytes.
     */
    override val length: Int
        get() = super.length + 2
}
