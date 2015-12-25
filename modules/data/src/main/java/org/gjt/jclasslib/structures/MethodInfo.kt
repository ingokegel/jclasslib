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
 * Describes a method in a ClassFile structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com), [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class MethodInfo : ClassMember() {

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
        super.debug("$message method with access flags $accessFlagsVerbose, name_index $nameIndex, descriptor_index $descriptorIndex, ${attributes.size} attributes")
    }

    override fun printAccessFlagsVerbose(accessFlags: Int): String {
        return printAccessFlagsVerbose(AccessFlag.METHOD_ACCESS_FLAGS, accessFlags)
    }

    companion object {

        /**
         * Factory method for creating MethodInfo structures from a DataInput.
         *
         * @param input        the DataInput from which to read the MethodInfo structure
         * @param classFile the parent class file of the structure to be created
         * @return the new MethodInfo structure
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        fun create(input: DataInput, classFile: ClassFile) = MethodInfo().apply {
            this.classFile = classFile
            this.read(input)
        }
    }

}
