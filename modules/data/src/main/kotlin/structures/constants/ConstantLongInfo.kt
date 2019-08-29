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
 * Describes a CONSTANT_Long_info constant pool data structure.
 */
class ConstantLongInfo(classFile: ClassFile) : ConstantLargeNumeric(classFile) {

    override val constantType: ConstantType
        get() = ConstantType.LONG

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = long.toString()

    /**
     * The long value of this constant pool entry.
     */
    var long: Long
        get() = (highBytes.toLong() shl 32) or (lowBytes.toLong() and 0xFFFFFFFF)
        set(number) {
            highBytes = (number ushr 32).toInt()
            lowBytes = (number and 0xFFFFFFFF).toInt()
        }

    override fun writeData(output: DataOutput) {
        output.writeByte(ConstantType.LONG.tag)
        super.writeData(output)
    }

}
