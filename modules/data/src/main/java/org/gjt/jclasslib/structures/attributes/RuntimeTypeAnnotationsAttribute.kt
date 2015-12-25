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
package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Common class for runtime type annotations.

 */
open class RuntimeTypeAnnotationsAttribute : AttributeInfo(), AnnotationHolder {

    /**
     * Runtime annotations associations of the parent structure
     */
    var runtimeAnnotations: Array<TypeAnnotation> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        val runtimeVisibleAnnotationsLength = input.readUnsignedShort()
        runtimeAnnotations = Array(runtimeVisibleAnnotationsLength) {
            TypeAnnotation().apply {
                this.classFile = classFile
                read(input)
            }
        }

        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(runtimeAnnotations.size)
        runtimeAnnotations.forEach { it.write(output) }

        if (isDebug) debug("wrote")
    }

    override fun getAttributeLength(): Int = 2 + runtimeAnnotations.sumBy { it.length }

    override fun getNumberOfAnnotations(): Int = runtimeAnnotations.size

}
