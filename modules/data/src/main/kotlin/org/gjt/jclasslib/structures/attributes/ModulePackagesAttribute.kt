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
 * Describes a ModulePackages attribute structure.
 */
class ModulePackagesAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * List of packages in the module.
     * Contains the indices of CONSTANT_Package_info structures in the constant pool.
     */
    var indices: IntArray = IntArray(0)

    override fun readData(input: DataInput) {
        val count = input.readUnsignedShort()
        indices = IntArray(count) {
            input.readUnsignedShort()
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(indices.size)
        indices.forEach { output.writeShort(it) }
    }

    override fun getAttributeLength(): Int =  2 + 2 * indices.size

    override val debugInfo: String
        get() = "with ${indices.size} packages"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "ModulePackages"
    }
}