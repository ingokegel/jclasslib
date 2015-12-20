/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.ConstantType
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import java.lang.Float.intBitsToFloat
import java.lang.Float.floatToIntBits

/**
 * Describes a CONSTANT_Float_info constant pool data structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class ConstantFloatInfo : ConstantNumeric() {

    override val constantType: ConstantType
        get() = ConstantType.CONSTANT_FLOAT

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

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        super.read(input)
        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {

        output.writeByte(ConstantType.CONSTANT_FLOAT.tag)
        super.write(output)
        if (isDebug) debug("wrote")
    }

    override fun debug(message: String) {
        super.debug("$message$ constantType with bytes $bytes")
    }

}
