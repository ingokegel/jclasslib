/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.constants.ConstantClassInfo
import org.gjt.jclasslib.structures.constants.ConstantNameAndTypeInfo

/**
 * Describes an EnclosingMethod attribute structure.
 */
class EnclosingMethodAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Constant pool index of the CONSTANT_Class_info
     * structure representing the innermost class that encloses the
     * declaration of the current class.
     */
    var classInfoIndex: Int = 0

    /**
     * Returns the constant that is referenced by the [classInfoIndex] index.
     */
    val classInfoConstant: ConstantClassInfo
        get() = classFile.getConstantPoolEntry(classInfoIndex, ConstantClassInfo::class)

    /**
     * Constant pool index of the CONSTANT_NameAndType_info
     * structure representing the name and the type of a method in the class
     * referenced by the class info index above.
     */
    var methodInfoIndex: Int = 0

    /**
     * Returns the constant that is referenced by the [methodInfoIndex] index.
     */
    val methodInfoConstant: ConstantNameAndTypeInfo
        get() = classFile.getConstantPoolEntry(methodInfoIndex, ConstantNameAndTypeInfo::class)

    override fun readData(input: DataInput) {
        classInfoIndex = input.readUnsignedShort()
        methodInfoIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(classInfoIndex)
        output.writeShort(methodInfoIndex)
    }

    override fun getAttributeLength(): Int = 4

    override val debugInfo: String
        get() = "with classInfoIndex $classInfoIndex and methodInfoIndex $methodInfoIndex"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "EnclosingMethod"
    }
}
