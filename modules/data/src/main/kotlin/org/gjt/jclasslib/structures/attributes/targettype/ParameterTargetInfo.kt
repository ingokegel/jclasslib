/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Target info for a TypeAnnotation structure with a parameter target.
 */
class ParameterTargetInfo : TargetInfo() {

    var typeParameterIndex: Int = 0

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        typeParameterIndex = input.readUnsignedByte()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(typeParameterIndex)
    }

    override val length: Int
        get() = 1

    override val verbose: String
        get() = "parameter index $typeParameterIndex"

    override val debugMessage: String
        get() = "ParameterTargetInfo with $verbose"
}
