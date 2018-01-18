/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes a ConstantValue attribute structure.
 */
class ConstantValueAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Constant pool index of the constant value.
     */
    var constantValueIndex: Int = 0

    override fun readData(input: DataInput) {
        constantValueIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(constantValueIndex)
    }

    override fun getAttributeLength(): Int = 2

    override val debugInfo: String
        get() = "with constantValueIndex $constantValueIndex"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "ConstantValue"
    }

}
