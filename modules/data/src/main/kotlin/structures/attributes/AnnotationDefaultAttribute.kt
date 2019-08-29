/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.elementvalues.ElementValue
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an  AnnotationDefault attribute structure.
 */
class AnnotationDefaultAttribute private constructor(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Constructor.
     * @param defaultValue the default element value
     * @param classFile the class file of which this structure is part of
     */
    constructor(defaultValue: ElementValue, classFile: ClassFile) : this(classFile) {
        this.defaultValue = defaultValue
    }

    internal constructor(classFile: ClassFile, input: DataInput) : this(classFile) {
        read(input)
    }

    /**
     * The default_value of this attribute.
     */
    lateinit var defaultValue: ElementValue

    override fun readData(input: DataInput) {
        defaultValue = ElementValue.create(input)
    }

    override fun writeData(output: DataOutput) {

        defaultValue.write(output)
    }

    override fun getAttributeLength(): Int = defaultValue.length

    override val debugInfo: String
        get() = ""

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "AnnotationDefault"
    }
}
