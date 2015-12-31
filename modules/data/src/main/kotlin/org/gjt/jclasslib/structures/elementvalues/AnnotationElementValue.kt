/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues

import org.gjt.jclasslib.structures.AnnotationData
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an  Annotation attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class AnnotationElementValue : ElementValue(ElementValueType.ANNOTATION), AnnotationData {

    /**
     * type_index of this annotation.
     */
    override var typeIndex: Int = 0

    /**
     * element value pair associations of the parent structure
     */
    override var elementValuePairEntries: Array<ElementValuePair> = emptyArray()

    override val entryName: String
        get() = "Annotation"

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        typeIndex = input.readUnsignedShort()
        val elementValuePairEntriesLength = input.readUnsignedShort()

        elementValuePairEntries = Array(elementValuePairEntriesLength) {
            ElementValuePair.create(input, classFile)
        }

        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        super.write(output)
        output.writeShort(typeIndex)
        output.writeShort(elementValuePairEntries.size)
        elementValuePairEntries.forEach { it.write(output) }

        debugWrite()
    }

    override val specificLength: Int
        get() = 4 + elementValuePairEntries.sumBy { it.length }

    override val debugInfo: String
        get() = "with ${elementValuePairEntries.size} value pair elements"
}
