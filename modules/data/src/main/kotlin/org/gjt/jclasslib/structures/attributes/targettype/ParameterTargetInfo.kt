/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import java.io.DataInput
import java.io.DataOutput

/**
 * Target info for a TypeAnnotation structure with a parameter target.
 */
class ParameterTargetInfo : TargetInfo() {

    /**
     * The index of the type parameter.
     */
    var typeParameterIndex: Int = 0

    override fun readData(input: DataInput) {
        typeParameterIndex = input.readUnsignedByte()
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(typeParameterIndex)
    }

    override val length: Int
        get() = 1

    override val verbose: String
        get() = "parameter index $typeParameterIndex"

    override val debugInfo: String
        get() = "with $verbose"
}
