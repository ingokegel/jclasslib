/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.InvalidByteCodeException
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * The class file can contain non-standard attributes that can be read, but that are not interpreted.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class UnknownAttribute(
        /**
         * Length of the unknown attribute.
         */
        val byteArrayLength : Int
) : AttributeInfo() {

    /**
     * Raw bytes of the unknown attribute.
     */
    var info: ByteArray = ByteArray(byteArrayLength)


    override fun readData(input: DataInput) {
        input.readFully(info)
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(attributeNameIndex)
        output.writeInt(getAttributeLength())
        if (javaClass == AttributeInfo::class.java) {
            output.write(info)
        }
    }

    override fun getAttributeLength(): Int {
        return info.size
    }

    override val debugInfo: String
        get() = "of reported type ${getAttributeName()}"

    private fun getAttributeName(): String {
        try {
            return classFile.getConstantPoolUtf8Entry(attributeNameIndex).string
        } catch (ex: InvalidByteCodeException) {
            return "(unknown)"
        }
    }
}