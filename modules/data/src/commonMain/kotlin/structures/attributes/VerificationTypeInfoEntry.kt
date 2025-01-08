/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput

/**
 * Describes an entry in a StackMapFrameEntry attribute structure.
 * @property type The verification type.
 */
open class VerificationTypeInfoEntry(val type: VerificationType) : SubStructure() {

    override fun readData(input: DataInput) {

    }

    override fun writeData(output: DataOutput) {
        output.writeByte(type.tag)
    }

    override val debugInfo: String
        get() = "of type $type"

    /**
     * The length of the entry in bytes.
     */
    override val length: Int
        get() = 1

    /**
     * Append the verbose representation to a string builder.
     */
    open fun appendTo(buffer: StringBuilder) {
        buffer.append(type)
    }
}
