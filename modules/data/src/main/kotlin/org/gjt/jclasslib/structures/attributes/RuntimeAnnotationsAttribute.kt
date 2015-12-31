/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.Annotation
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Base class for runtime annotations.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
abstract class RuntimeAnnotationsAttribute : AttributeInfo(), AnnotationHolder {

    /**
     * Runtime annotations associations of the parent
     */
    var runtimeAnnotations: Array<Annotation> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        val runtimeVisibleAnnotationsLength = input.readUnsignedShort()
        runtimeAnnotations = Array(runtimeVisibleAnnotationsLength) {
            Annotation().apply {
                this.classFile = classFile
                this.read(input)

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

    override fun getAttributeLength(): Int = 2 + runtimeAnnotations.sumBy { it.length }

    override fun getNumberOfAnnotations(): Int = runtimeAnnotations.size

}
