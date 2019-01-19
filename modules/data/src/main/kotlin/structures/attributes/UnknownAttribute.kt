/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException
import java.io.DataInput
import java.io.DataOutput

/**
 * The class file can contain non-standard attributes that can be read, but that are not interpreted.
 * @property byteArrayLength Length of the unknown attribute.
 */
class UnknownAttribute(val byteArrayLength: Int, classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Raw bytes of the unknown attribute.
     */
    var info: ByteArray = ByteArray(byteArrayLength)

    override fun readData(input: DataInput) {
        // Uncomment to print out the attribute name and size
        //println(classFile.getConstantPoolUtf8Entry(attributeNameIndex).string)
        //println(byteArrayLength)

        input.readFully(info)
    }

    override fun writeData(output: DataOutput) {
        output.write(info)
    }

    override fun getAttributeLength(): Int = info.size

    override val debugInfo: String
        get() = "of reported type ${getAttributeName()}"

    private fun getAttributeName(): String {
        return try {
            classFile.getConstantPoolUtf8Entry(attributeNameIndex).string
        } catch (ex: InvalidByteCodeException) {
            "(unknown)"
        }
    }
}