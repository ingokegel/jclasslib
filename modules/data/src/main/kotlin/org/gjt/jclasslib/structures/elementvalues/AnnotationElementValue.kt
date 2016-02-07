/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues

import org.gjt.jclasslib.structures.AnnotationData
import org.gjt.jclasslib.structures.emptyArraySingleton
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an Annotation attribute structure.
 */
class AnnotationElementValue : ElementValue(ElementValueType.ANNOTATION), AnnotationData {

    /**
     * type_index of this annotation.
     */
    override var typeIndex: Int = 0

    /**
     * element value pair associations of the parent structure
     */
    override var elementValuePairEntries: Array<ElementValuePair> = emptyArraySingleton()

    override val entryName: String
        get() = "Annotation"

    override fun readData(input: DataInput) {
        typeIndex = input.readUnsignedShort()
        val elementValuePairEntriesLength = input.readUnsignedShort()

        elementValuePairEntries = Array(elementValuePairEntriesLength) {
            ElementValuePair(input)
        }
    }

    override fun writeData(output: DataOutput) {
        super.writeData(output)
        output.writeShort(typeIndex)
        output.writeShort(elementValuePairEntries.size)
        elementValuePairEntries.forEach { it.write(output) }
    }

    override val length: Int
        get() = super.length + 4 + elementValuePairEntries.sumBy { it.length }

    override val debugInfo: String
        get() = "with ${elementValuePairEntries.size} value pair elements"
}
