/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues

import org.gjt.jclasslib.structures.Structure
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an  ElementValue attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
abstract class ElementValue(val elementValueType: ElementValueType) : Structure() {

    abstract val entryName: String
    protected abstract val specificLength: Int

    override fun writeData(output: DataOutput) {
        output.writeByte(elementValueType.tag)
    }

    val length: Int
        get() = 1 + specificLength

    companion object {
        /**
         * Factory for creating ElementValue structures.
         * @param input the DataInput from which to read the ElementValue structure
         */
        fun create(input: DataInput): ElementValue {
            val tag = input.readUnsignedByte()
            val elementValueType = ElementValueType.getFromTag(tag)
            return elementValueType.createEntry().apply {
                this.read(input)
            }
        }
    }

}
