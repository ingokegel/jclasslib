/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures

import org.gjt.jclasslib.structures.elementvalues.ElementValuePair

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an  Annotation attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class Annotation : AbstractStructure(), AnnotationData {

    private var typeIndex: Int = 0
    private var elementValuePairEntries: Array<ElementValuePair> = emptyArray()


    override fun getElementValuePairEntries(): Array<ElementValuePair> {
        return elementValuePairEntries
    }

    /**
     * Set the list of element value pair  associations of the parent
     * structure as an array of ElementValuePair structures.

     * @param elementValuePairEntries the array
     */
    fun setElementValuePairEntries(elementValuePairEntries: Array<ElementValuePair>) {
        this.elementValuePairEntries = elementValuePairEntries
    }

    override fun getTypeIndex(): Int {
        return typeIndex
    }

    /**
     * Set the type_index of this annotation.

     * @param typeIndex the type_index
     */
    fun setTypeIndex(typeIndex: Int) {
        this.typeIndex = typeIndex
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {

        typeIndex = input.readUnsignedShort()
        val elementValuePairEntriesLength = input.readUnsignedShort()

        elementValuePairEntries = Array(elementValuePairEntriesLength) {
            ElementValuePair.create(input, classFile)
        }

        if (isDebug) debug("read")
    }

    val length: Int
        get() = 4 + elementValuePairEntries.sumBy { it.length }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {

        output.writeShort(typeIndex)
        output.writeShort(elementValuePairEntries.size)
        elementValuePairEntries.forEach { it.write(output) }

        if (isDebug) debug("wrote")
    }

    override fun debug(message: String) {
        super.debug("$message Annotation with ${getLength(elementValuePairEntries)} value pair elements")
    }

}
