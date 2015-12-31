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
 * Describes a BootstrapMethods attribute structure.
 */
class BootstrapMethodsAttribute : AttributeInfo() {

    /**
     * Bootstrap method references in the BootstrapMethodsAttribute structure
     */
    var methods: Array<BootstrapMethodsEntry> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        val numberOfRefs = input.readUnsignedShort()
        methods = Array(numberOfRefs) {
            BootstrapMethodsEntry.create(input, classFile)
        }

        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(methods.size)
        methods.forEach { it.write(output) }

        debugWrite()
    }

    override fun getAttributeLength(): Int = 2 + methods.sumBy { it.length }

    override val debugInfo: String
        get() = "with ${methods.size} references"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        val ATTRIBUTE_NAME = "BootstrapMethods"

    }
}
