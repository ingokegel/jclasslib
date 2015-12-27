/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AbstractStructure
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Type path entry for a TypeAnnotation structure.
 */
class TypePathEntry : AbstractStructure() {

    lateinit var typePathKind: TypePathKind
    var typeArgumentIndex: Int = 0

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        typePathKind = TypePathKind.getFromTag(input.readUnsignedByte())
        typeArgumentIndex = input.readUnsignedByte()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(typePathKind.tag)
        output.writeByte(typeArgumentIndex)
    }

}
