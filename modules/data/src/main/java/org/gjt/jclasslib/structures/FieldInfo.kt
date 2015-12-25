/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes a field in a ClassFile structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com), [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class FieldInfo : ClassMember() {

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        super.read(input)

        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        super.write(output)

        if (isDebug) debug("wrote")
    }

    override fun debug(message: String) {
        super.debug("$message field with access flags $accessFlagsVerbose, name_index $nameIndex, descriptor_index $descriptorIndex, ${attributes.size} attributes")
    }

    override fun printAccessFlagsVerbose(accessFlags: Int): String {
        return printAccessFlagsVerbose(AccessFlag.FIELD_ACCESS_FLAGS, accessFlags)
    }

    companion object {

        /**
         * Factory method for creating FieldInfo structures from a DataInput.
         * @param input the DataInput from which to read the FieldInfo structure
         * @param classFile the parent class file of the structure to be created
         * @return the new FieldInfo structure
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        fun create(input: DataInput, classFile: ClassFile) = FieldInfo().apply {
            this.classFile = classFile
            this.read(input)
        }
    }

}
