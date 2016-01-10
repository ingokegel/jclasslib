/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an entry in a LineNumberTable attribute structure.
 */
class LineNumberTableEntry : SubStructure() {

    /**
     * start_pc of this line number association.
     */
    var startPc: Int = 0

    /**
     * Line number of this line number association.
     */
    var lineNumber: Int = 0

    override fun readData(input: DataInput) {
        startPc = input.readUnsignedShort()
        lineNumber = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(startPc)
        output.writeShort(lineNumber)
    }

    override val debugInfo: String
        get() = "with startPc $startPc, lineNumber $lineNumber"

    override val length: Int
        get() = 4

}
