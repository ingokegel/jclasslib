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

/**
 * Describes a CONSTANT_Long_info constant pool data structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class ConstantLongInfo : ConstantLargeNumeric() {

    override val constantType: ConstantType
        get() = ConstantType.LONG

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = long.toString()

    /**
     * The long value of this constant pool entry.
     * @param number the value
     */
    var long: Long
        get() = (highBytes.toLong() shl 32) or (lowBytes.toLong() and 0xFFFFFFFF)
        set(number) {
            highBytes = (number ushr 32).toInt()
            lowBytes = (number and 0xFFFFFFFF).toInt()
        }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        super.read(input)
        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(ConstantType.LONG.tag)
        super.write(output)
        if (isDebug) debug("wrote")
    }

    override fun debug(message: String) {
        super.debug("$message $constantType with high_bytes $highBytes and low_bytes $lowBytes")
    }

}
