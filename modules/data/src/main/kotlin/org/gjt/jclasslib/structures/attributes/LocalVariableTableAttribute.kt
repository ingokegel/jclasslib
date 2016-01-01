/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFile
import java.io.DataInput

/**
 * Describes an LocalVariableTable attribute structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com), [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class LocalVariableTableAttribute(classFile: ClassFile) : LocalVariableCommonAttribute<LocalVariableTableEntry>(classFile) {

    override var localVariableEntries: Array<LocalVariableTableEntry> = emptyArray()

    override fun readData(input: DataInput) {
        val localVariableTableLength = input.readUnsignedShort()
        localVariableEntries = Array(localVariableTableLength) {
            LocalVariableTableEntry().apply {
                read(input)
            }
        }
    }

    override fun getAttributeLength(): Int {
        return super.getAttributeLength() + localVariableEntries.size * LocalVariableCommonEntry.LENGTH
    }

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        val ATTRIBUTE_NAME = "LocalVariableTable"
    }
}
