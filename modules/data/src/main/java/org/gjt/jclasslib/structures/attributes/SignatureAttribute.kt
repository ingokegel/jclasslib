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
 * Describes an  Signature attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class SignatureAttribute : AttributeInfo() {

    /**
     * Constant pool index of the CONSTANT_Utf8_info
     * structure representing the signature.
     */
    var signatureIndex: Int = 0

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        signatureIndex = input.readUnsignedShort()
        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(signatureIndex)
        if (isDebug) debug("wrote")
    }

    override fun getAttributeLength(): Int = 2

    override fun debug(message: String) {
        super.debug("$message Signature attribute with signature index $signatureIndex")
    }

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        val ATTRIBUTE_NAME = "Signature"
    }
}
