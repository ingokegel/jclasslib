/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues

import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an EnumElementValue attribute structure.
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

    override val length: Int
        get() = super.length + 4

    override fun readData(input: DataInput) {
        typeNameIndex = input.readUnsignedShort()
        constNameIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        super.writeData(output)
        output.writeShort(typeNameIndex)
        output.writeShort(constNameIndex)
    }

    override val debugInfo: String
        get() = "with typeNameIndex $typeNameIndex, constNameIndex $constNameIndex"

    override val entryName: String
        get() = "EnumElement"

}
