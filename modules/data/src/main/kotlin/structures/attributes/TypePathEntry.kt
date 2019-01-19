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
 * Type path entry for a TypeAnnotation structure.
 */
class TypePathEntry private constructor() : SubStructure() {

    /**
     * Constructor.
     * @param typePathKind the type path kind
     */
    constructor(typePathKind: TypePathKind) : this() {
        this.typePathKind = typePathKind
    }

    internal constructor(input: DataInput) : this() {
        read(input)
    }

    /**
     * The type path kind.
     */
    lateinit var typePathKind: TypePathKind

    /**
     * The index of the type argument.
     */
    var typeArgumentIndex: Int = 0

    override fun readData(input: DataInput) {
        typePathKind = TypePathKind.getFromTag(input.readUnsignedByte())
        typeArgumentIndex = input.readUnsignedByte()
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(typePathKind.tag)
        output.writeByte(typeArgumentIndex)
    }

    override val debugInfo: String
        get() = "with typePathKind $typePathKind, typeArgumentIndex $typeArgumentIndex"

    override val length: Int
        get() = 2
}
