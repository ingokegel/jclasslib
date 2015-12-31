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

    override var typeIndex: Int = 0
    override var elementValuePairEntries: Array<ElementValuePair> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        typeIndex = input.readUnsignedShort()
        val elementValuePairEntriesLength = input.readUnsignedShort()
        elementValuePairEntries = Array(elementValuePairEntriesLength) {
            ElementValuePair.create(input, classFile)
        }

        debugRead()
    }

    val length: Int
        get() = 4 + elementValuePairEntries.sumBy { it.length }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(typeIndex)
        output.writeShort(elementValuePairEntries.size)
        elementValuePairEntries.forEach { it.write(output) }

        debugWrite()
    }

    override val debugMessage: String
        get() = "Annotation with ${elementValuePairEntries.size} value pair elements"
}
