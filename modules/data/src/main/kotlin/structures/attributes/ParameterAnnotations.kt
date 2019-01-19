/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.Annotation
import org.gjt.jclasslib.structures.emptyArraySingleton
import java.io.DataInput
import java.io.DataOutput

/**
 * Common class for runtime parameter annotations.
 */
class ParameterAnnotations : SubStructure() {

    /**
     * Runtime annotations associations of the parent structure
     */
    var runtimeAnnotations: Array<Annotation> = emptyArraySingleton()

    override fun readData(input: DataInput) {
        val runtimeVisibleAnnotationsLength = input.readUnsignedShort()

        runtimeAnnotations = Array(runtimeVisibleAnnotationsLength) {
            Annotation().apply {
                read(input)
            }
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(runtimeAnnotations.size)
        runtimeAnnotations.forEach { it.write(output) }
    }

    override val debugInfo: String
        get() = ""

    /**
     * The length of the structure in bytes.
     */
    override val length: Int
        get() = 2 + runtimeAnnotations.sumBy { it.length }

}
