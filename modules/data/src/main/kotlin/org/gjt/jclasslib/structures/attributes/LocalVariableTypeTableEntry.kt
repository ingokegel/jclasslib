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
 * Describes an  LocalVariableTypeTableEntry attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class LocalVariableTypeTableEntry : LocalVariableCommonEntry() {

    companion object {
        /**
         * Factory method for creating LocalVariableTypeTableEntry structures.
         * @param input  the DataInput from which to read the
         * @param classFile the parent class file of the structure to be created
         */
        fun create(input: DataInput, classFile: ClassFile) = LocalVariableTypeTableEntry().apply {
            this.classFile = classFile
            this.read(input)

        }
    }
}
