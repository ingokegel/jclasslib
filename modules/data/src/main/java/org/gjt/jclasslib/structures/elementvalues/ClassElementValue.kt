/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues

import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an  ClassElementValue attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class ClassElementValue : ElementValue(ElementValueType.CLASS) {
    /**
     * class_info_index of this element value entry.
     */
    var classInfoIndex: Int = 0

    override val specificLength: Int
        get() = 2

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        classInfoIndex = input.readUnsignedShort()

        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        super.write(output)
        output.writeShort(classInfoIndex)

        if (isDebug) debug("wrote")
    }

    override fun debug(message: String) {
        super.debug("$message ClassElementValue with class_info_index $classInfoIndex")
    }

    override val entryName: String
        get() = "ClassElement"

}
