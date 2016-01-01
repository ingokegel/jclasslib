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
 * Describes an  ConstElementValue attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class ConstElementValue(elementValueType: ElementValueType) : ElementValue(elementValueType) {
    /**
     * const_value_index of this element value entry.
     */
    var constValueIndex: Int = 0

    override val specificLength: Int
        get() = 2

    override fun readData(input: DataInput) {
        constValueIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        super.writeData(output)
        output.writeShort(constValueIndex)
    }

    override val debugInfo: String
        get() = "with constValueIndex $constValueIndex"

    override val entryName: String
        get() = "ConstElement"

}
