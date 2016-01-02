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
import java.lang.Float.floatToIntBits
import java.lang.Float.intBitsToFloat

/**
 * Describes a CONSTANT_Float_info constant pool data structure.
 */
class ConstantFloatInfo(classFile: ClassFile) : ConstantNumeric(classFile) {

    override val constantType: ConstantType
        get() = ConstantType.FLOAT

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = float.toString()

    /**
     * Float value of this constant pool entry.
     */
    var float: Float
        get() = intBitsToFloat(bytes)
        set(number) {
            bytes = floatToIntBits(number)
        }

    override fun writeData(output: DataOutput) {
        output.writeByte(ConstantType.FLOAT.tag)
        super.writeData(output)
    }

    override val debugInfo: String
        get() = "with bytes $bytes"
}
