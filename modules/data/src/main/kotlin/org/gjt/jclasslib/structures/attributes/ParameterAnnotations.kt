/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AbstractStructure
import org.gjt.jclasslib.structures.Annotation
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Common class for runtime parameter annotations.
 */
class ParameterAnnotations : AbstractStructure() {

    /**
     * Runtime annotations associations of the parent structure
     */
    var runtimeAnnotations: Array<Annotation> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        val runtimeVisibleAnnotationsLength = input.readUnsignedShort()

        runtimeAnnotations = Array(runtimeVisibleAnnotationsLength) {
            Annotation().apply {
                this.classFile = classFile
                read(input)
            }
        }

        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(runtimeAnnotations.size)
        runtimeAnnotations.forEach { it.write(output) }

        debugWrite()
    }

    override val debugInfo: String
        get() = ""

    val length: Int
        get() = 2 + runtimeAnnotations.sumBy { length }

}
