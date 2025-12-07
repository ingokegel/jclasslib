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
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.emptyArraySingleton

/**
 * Describes a BootstrapMethods attribute structure.
 */
class BootstrapMethodsAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Bootstrap method references in the BootstrapMethodsAttribute structure
     */
    var methods: Array<BootstrapMethodsEntry> = emptyArraySingleton()

    override fun readData(input: DataInput) {
        val numberOfRefs = input.readUnsignedShort()
        methods = Array(numberOfRefs) {
            BootstrapMethodsEntry(classFile).apply {
                read(input)
            }
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(methods.size)
        methods.forEach { it.write(output) }
    }

    override fun isConstantUsed(constant: Constant, classFile: ClassFile): Boolean {
        return super.isConstantUsed(constant, classFile) ||
                methods.any { it.isConstantUsed(constant, classFile) }
    }

    override fun getAttributeLength(): Int = 2 + methods.sumOf { it.length }

    override val debugInfo: String
        get() = "with ${methods.size} references"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "BootstrapMethods"
    }
}
