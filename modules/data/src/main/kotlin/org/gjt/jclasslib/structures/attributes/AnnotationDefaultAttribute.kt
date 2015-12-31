/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.elementvalues.ElementValue

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an  AnnotationDefault attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class AnnotationDefaultAttribute constructor(): AttributeInfo() {

    /**
     * The default_value of this attribute.
     */
    lateinit var defaultValue: ElementValue

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        defaultValue = ElementValue.create(input, classFile)

        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {

        defaultValue.write(output)

        debugWrite()
    }

    override fun getAttributeLength(): Int = defaultValue.length

    override val debugInfo: String
        get() = ""

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        val ATTRIBUTE_NAME = "AnnotationDefault"
    }
}
