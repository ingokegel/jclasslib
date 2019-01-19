/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.AbstractConstant
import org.gjt.jclasslib.structures.ClassFile
import java.io.DataInput
import java.io.DataOutput

/**
 * Base class for large numeric constant pool data structures.
 */
abstract class ConstantLargeNumeric(classFile: ClassFile) : AbstractConstant(classFile) {

    /**
     * The high_bytes field of this constant pool entry.
     */
    var highBytes: Int = 0

    /**
     * The low_bytes field of this constant pool entry.
     */
    var lowBytes: Int = 0

    /**
     * Get the the high_bytes field of this constant pool
     * entry as a hex string.
     */
    val formattedHighBytes: String
        get() = formatBytes(highBytes)

    /**
     * Get the the low_bytes field of this constant pool
     * entry as a hex string.
     */
    val formattedLowBytes: String
        get() = formatBytes(lowBytes)

    override fun readData(input: DataInput) {

        highBytes = input.readInt()
        lowBytes = input.readInt()
    }

    override fun writeData(output: DataOutput) {

        output.writeInt(highBytes)
        output.writeInt(lowBytes)
    }

    override val debugInfo: String
        get() = "with high_bytes $highBytes and low_bytes $lowBytes"

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantLargeNumeric) {
            return false
        }
        return super.equals(other) && other.highBytes == highBytes && other.lowBytes == lowBytes
    }

    override fun hashCode(): Int = super.hashCode() xor highBytes xor lowBytes

}
