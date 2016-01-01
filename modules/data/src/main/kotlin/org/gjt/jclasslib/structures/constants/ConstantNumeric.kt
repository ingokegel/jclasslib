/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.CPInfo
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Base class for numeric constant pool data structures.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
abstract class ConstantNumeric : CPInfo() {

    /**
     * Bytes field of this constant pool entry.
     */
    var bytes: Int = 0

    /**
     * Get the the bytes field of this constant pool
     * entry as a hex string.
     */
    val formattedBytes: String
        get() = printBytes(bytes)

    override fun readData(input: DataInput) {
        bytes = input.readInt()
    }

    override fun writeData(output: DataOutput) {
        output.writeInt(bytes)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantNumeric) {
            return false
        }
        return super.equals(other) && other.bytes == bytes
    }

    override fun hashCode(): Int = super.hashCode() xor bytes

}
