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
 * Describes an  ElementValuePair attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class ElementValuePair : AbstractStructure() {

    /**
     * element_name_index of this element value pair.
     */
    var elementNameIndex: Int = 0

    /**
     * element_value of this element value pair.
     */
    lateinit var elementValue: ElementValue

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        elementNameIndex = input.readUnsignedShort()
        elementValue = ElementValue.create(input, classFile)

        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(elementNameIndex)
        elementValue.write(output)

        debugWrite()
    }

    override val debugInfo: String
        get() = "with elementNameIndex $elementNameIndex"

    val length: Int
        get() = 2 + elementValue.length

    val entryName: String
        get() = "ElementValuePair"

    companion object {

        /**
         * Factory for creating ElementValuePair structures.
         * @param in the DataInput from which to read the ElementValuePair structure
         * @param classFile the parent class file of the structure to be created
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        fun create(`in`: DataInput, classFile: ClassFile) = ElementValuePair().apply {
            this.classFile = classFile
            this.read(`in`)

        }
    }
}
