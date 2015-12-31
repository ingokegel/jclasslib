/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues

import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an  EnumElementValue attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class EnumElementValue : ElementValue(ElementValueType.ENUM) {
    /**
     * type_name_index of this element value entry.
     */
    var typeNameIndex: Int = 0

    /**
     * const_name_index of this element value entry.
     */
    var constNameIndex: Int = 0

    override val specificLength: Int
        get() = 4

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        typeNameIndex = input.readUnsignedShort()
        constNameIndex = input.readUnsignedShort()

        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        super.write(output)
        output.writeShort(typeNameIndex)
        output.writeShort(constNameIndex)

        debugWrite()
    }

    override val debugInfo: String
        get() = "with typeNameIndex $typeNameIndex, constNameIndex $constNameIndex"

    override val entryName: String
        get() = "EnumElement"

}
