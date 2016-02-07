/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues

import org.gjt.jclasslib.structures.attributes.SubStructure
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an ElementValue attribute structure.
 * @property elementValueType The type of the element value.
 */
abstract class ElementValue(val elementValueType: ElementValueType) : SubStructure() {

    /**
     * Name of the entry.
     */
    abstract val entryName: String

    override fun writeData(output: DataOutput) {
        output.writeByte(elementValueType.tag)
    }

    /**
     * Length of the entry in bytes.
     */
    override val length: Int
        get() = 1

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
