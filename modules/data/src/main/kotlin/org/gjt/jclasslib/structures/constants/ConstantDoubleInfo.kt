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
import java.lang.Double.doubleToLongBits
import java.lang.Double.longBitsToDouble

/**
 * Describes a CONSTANT_Double_info constant pool data structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class ConstantDoubleInfo : ConstantLargeNumeric() {

    override val constantType: ConstantType
        get() = ConstantType.DOUBLE

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = double.toString()

    /**
     * The double value of this constant pool entry.
     */
    var double: Double
        get() {
            val longBits = highBytes.toLong() shl 32 or (lowBytes.toLong() and 0xFFFFFFFFL)
            return longBitsToDouble(longBits)
        }
        set(number) {
            val longBits = doubleToLongBits(number)
            highBytes = (longBits ushr 32 and 0xFFFFFFFFL).toInt()
            lowBytes = (longBits and 0xFFFFFFFFL).toInt()
        }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        super.read(input)
        debugRead()
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(ConstantType.DOUBLE.tag)
        super.write(output)
        debugWrite()
    }

}
