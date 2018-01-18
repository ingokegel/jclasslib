/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.emptyArraySingleton
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an InnerClasses attribute structure.
 */
class InnerClassesAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Inner classes of the parent ClassFile structure
     */
    var classes: Array<InnerClassesEntry> = emptyArraySingleton()

    override fun readData(input: DataInput) {
        val numberOfClasses = input.readUnsignedShort()
        classes = Array(numberOfClasses) {
            InnerClassesEntry().apply {
                read(input)
            }
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(classes.size)
        classes.forEach { it.write(output) }
    }

    override fun getAttributeLength(): Int = 2 + classes.sumBy { it.length }

    override val debugInfo: String
        get() = "with ${classes.size} classes"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "InnerClasses"
    }

}
