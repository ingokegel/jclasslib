/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.IOException

/**
 * Describes an entry in a LocalVariableTableEntry attribute structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com), [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class LocalVariableTableEntry : LocalVariableCommonEntry() {

    override val debugMessage: String
        get() = "LocalVariableTable entry with start_pc $startPc, length $length, name_index $nameIndex, descriptor_index $descriptorOrSignatureIndex, index $index"

    companion object {
        /**
         * Factory method for creating LocalVariableTableEntry structures.
         * @param input the DataInput from which to read the LocalVariableTableEntry structure
         * @param classFile the parent class file of the structure to be created
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        fun create(input: DataInput, classFile: ClassFile) = LocalVariableTableEntry().apply {
            this.classFile = classFile
            this.read(input)
        }
    }
}
