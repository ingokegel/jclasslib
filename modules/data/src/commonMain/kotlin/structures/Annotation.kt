/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/
package org.gjt.jclasslib.structures

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import org.gjt.jclasslib.structures.attributes.SubStructure
import org.gjt.jclasslib.structures.elementvalues.ElementValuePair

/**
 * Describes an Annotation attribute structure.
 */
class Annotation : SubStructure(), AnnotationData {

    override var typeIndex: Int = 0
    override var elementValuePairEntries: Array<ElementValuePair> = emptyArraySingleton()

    override fun readData(input: DataInput) {
        typeIndex = input.readUnsignedShort()
        val elementValuePairEntriesLength = input.readUnsignedShort()
        elementValuePairEntries = Array(elementValuePairEntriesLength) {
            ElementValuePair(input)
        }
    }

    /**
     * Length of the structure in bytes.
     */
    override val length: Int
        get() = 4 + elementValuePairEntries.sumOf { it.length }

    override fun writeData(output: DataOutput) {
        output.writeShort(typeIndex)
        output.writeShort(elementValuePairEntries.size)
        elementValuePairEntries.forEach { it.write(output) }
    }

    override fun getUsedConstantPoolIndices() = intArrayOf(typeIndex)

    override fun isConstantUsed(constant: Constant, classFile: ClassFile): Boolean {
        return super.isConstantUsed(constant, classFile) ||
                elementValuePairEntries.any { it.isConstantUsed(constant, classFile) }
    }

    override val debugInfo: String
        get() = "with ${elementValuePairEntries.size} value pair elements"
}
