/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues

import org.gjt.jclasslib.structures.AbstractStructure
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an  ElementValue attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
abstract class ElementValue(val elementValueType: ElementValueType) : AbstractStructure() {

    abstract val entryName: String
    protected abstract val specificLength: Int

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(elementValueType.tag)

        debugWrite()
    }

    val length: Int
        get() = 1 + specificLength

    companion object {
        /**
         * Factory for creating ElementValue structures.
         * @param input the DataInput from which to read the ElementValue structure
         * @param classFile the parent class file of the structure to be created
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        fun create(input: DataInput, classFile: ClassFile): ElementValue {
            val tag = input.readUnsignedByte()
            val elementValueType = ElementValueType.getFromTag(tag)
            return elementValueType.createEntry().apply {
                this.classFile = classFile
                this.read(input)
            }
        }
    }

}
