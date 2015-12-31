/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataOutput
import java.io.IOException

/**
 * Contains common attributes to a local variable table attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
abstract class LocalVariableCommonAttribute<T : LocalVariableCommonEntry> : AttributeInfo() {

    /**
     * Local variable associations of the parent code attribute
     */
    abstract var localVariableEntries: Array<T>

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(localVariableEntries.size)
        localVariableEntries.forEach { it.write(output) }

        debugWrite()
    }

    override val debugInfo: String
        get() = "with ${localVariableEntries.size} entries"

    override fun getAttributeLength(): Int = 2

}
