/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes a ModuleMainClass attribute structure.
 */
class ModuleMainClassAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Constant pool index of the CONSTANT_Class_info structure containing the module main class.
     */
    var mainClassIndex: Int = 0

    /**
     * Returns the constant that is referenced by the [mainClassIndex] index.
     */
    val mainClassConstant: ConstantUtf8Info
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolUtf8Entry(mainClassIndex)

    override fun readData(input: DataInput) {
        mainClassIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(mainClassIndex)
    }

    override fun getAttributeLength(): Int =  2

    override val debugInfo: String
        get() = ""

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "ModuleMainClass"
    }
}