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
 * Describes a Synthetic attribute structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class SyntheticAttribute : AttributeInfo() {

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        debugWrite()
    }

    override fun getAttributeLength(): Int = 0

    override val debugMessage: String
        get() = "Synthetic attribute"

    companion object {

        /** Name of the attribute as in the corresponding constant pool entry.  */
        val ATTRIBUTE_NAME = "Synthetic"
    }

}
