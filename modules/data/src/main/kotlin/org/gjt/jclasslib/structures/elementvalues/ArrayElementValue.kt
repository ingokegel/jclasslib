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
 * Describes an  ArrayElementValue attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class ArrayElementValue : ElementValue(ElementValueType.ARRAY) {
    /**
     * Element values associations of this entry.
     */
    var elementValueEntries: Array<ElementValue> = emptyArray()

    override val specificLength: Int
        get() = 2 + elementValueEntries.sumBy { it.length }

    override fun readData(input: DataInput) {
        val elementValueEntriesLength = input.readUnsignedShort()
        elementValueEntries = Array(elementValueEntriesLength) {
            ElementValue.create(input)
        }
    }

    override fun writeData(output: DataOutput) {
        super.writeData(output)
        output.writeShort(elementValueEntries.size)
        elementValueEntries.forEach { it.write(output) }
    }

    override val debugInfo: String
        get() = "with ${elementValueEntries.size} entries"

    override val entryName: String
        get() = "ArrayElement"

}
