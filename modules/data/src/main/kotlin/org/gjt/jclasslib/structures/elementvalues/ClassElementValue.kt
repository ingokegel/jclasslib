/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues

import java.io.DataInput
import java.io.DataOutput

/**
 * Describes a ClassElementValue attribute structure.
 */
class ClassElementValue : ElementValue(ElementValueType.CLASS) {
    /**
     * class_info_index of this element value entry.
     */
    var classInfoIndex: Int = 0

    override val length: Int
        get() = super.length + 2

    override fun readData(input: DataInput) {
        classInfoIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        super.writeData(output)
        output.writeShort(classInfoIndex)
    }

    override val debugInfo: String
        get() = "with classInfoIndex $classInfoIndex"

    override val entryName: String
        get() = "ClassElement"

}
