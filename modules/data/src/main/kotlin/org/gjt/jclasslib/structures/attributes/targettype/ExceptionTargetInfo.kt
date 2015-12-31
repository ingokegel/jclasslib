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
 * Target info for a TypeAnnotation structure with an exception table link.
 */
class ExceptionTargetInfo : TargetInfo() {

    var exceptionTableIndex: Int = 0

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        exceptionTableIndex = input.readUnsignedShort()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(exceptionTableIndex)
    }

    override val length: Int
        get() = 2

    override val verbose: String
        get() = "<a href=\"E$exceptionTableIndex\">exception table entry $exceptionTableIndex</a>"

    override val debugMessage: String
        get() = "ExceptionTargetInfo with exceptionTableIndex $exceptionTableIndex"
}
