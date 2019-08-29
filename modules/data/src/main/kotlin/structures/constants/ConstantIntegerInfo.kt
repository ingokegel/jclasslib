/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.ConstantType
import org.gjt.jclasslib.structures.InvalidByteCodeException
import java.io.DataOutput

/**
 * Describes a CONSTANT_Integer_info constant pool data structure.
 */
class ConstantIntegerInfo(classFile: ClassFile) : ConstantNumeric(classFile) {

    override val constantType: ConstantType
        get() = ConstantType.INTEGER

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = int.toString()

    /**
     * Int value of this constant pool entry.
     */
    var int: Int
        get() = bytes
        set(number) {
            bytes = number
        }

    override fun writeData(output: DataOutput) {
        output.writeByte(ConstantType.INTEGER.tag)
        super.writeData(output)
    }

    override val debugInfo: String
        get() = "with bytes $bytes"
}
