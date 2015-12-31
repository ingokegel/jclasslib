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
 * Common class for runtime parameter annotations.
 */
abstract class RuntimeParameterAnnotationsAttribute : AttributeInfo() {

    /**
     * Parameter annotations
     */
    var parameterAnnotations: Array<ParameterAnnotations> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        val numParameters = input.readUnsignedByte()
        parameterAnnotations = Array(numParameters) {
            ParameterAnnotations().apply { read(input) }
        }

        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(parameterAnnotations.size)
        parameterAnnotations.forEach { it.write(output) }

        debugWrite()
    }

    override val debugInfo: String
        get() = "with ${parameterAnnotations.size} entries"

    override fun getAttributeLength(): Int = 1 + parameterAnnotations.sumBy { it.length }

}
