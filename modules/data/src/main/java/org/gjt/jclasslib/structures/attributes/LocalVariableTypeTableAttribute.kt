/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.IOException

/**
 * Describes an  LocalVariableTypeTable attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class LocalVariableTypeTableAttribute : LocalVariableCommonAttribute<LocalVariableTypeTableEntry>() {

    override var localVariableEntries: Array<LocalVariableTypeTableEntry> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        val localVariableTypeTableLength = input.readUnsignedShort()
        localVariableEntries = Array(localVariableTypeTableLength) {
            LocalVariableTypeTableEntry.create(input, classFile)
        }

        if (isDebug) debug("read")
    }

    override fun getAttributeLength(): Int = super.getAttributeLength() + localVariableEntries.size * LocalVariableCommonEntry.LENGTH

    override fun debug(message: String) {
        super.debug("$message LocalVariableTypeTable attribute with ${localVariableEntries.size} entries")
    }

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        val ATTRIBUTE_NAME = "LocalVariableTypeTable"
    }
}
