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
 * Contains common attributes to a local variable table entry structure.
 */
class LocalVariableEntry : SubStructure() {

    /**
     * start_pc of this local variable association.
     */
    var startPc: Int = 0

    /**
     * Length in bytes of this local variable association.
     */
    var targetLength: Int = 0

    /**
     * Index of the constant pool entry containing the name of this
     * local variable.
     */
    var nameIndex: Int = 0

    /**
     * Index of the constant pool entry containing the descriptor of this
     * local variable.
     */
    var descriptorOrSignatureIndex: Int = 0

    /**
     * Index of this local variable.
     */
    var index: Int = 0

    override fun readData(input: DataInput) {
        startPc = input.readUnsignedShort()
        targetLength = input.readUnsignedShort()
        nameIndex = input.readUnsignedShort()
        descriptorOrSignatureIndex = input.readUnsignedShort()
        index = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(startPc)
        output.writeShort(targetLength)
        output.writeShort(nameIndex)
        output.writeShort(descriptorOrSignatureIndex)
        output.writeShort(index)
    }

    override val debugInfo: String
        get() = "with startPc $startPc, length $targetLength, nameIndex $nameIndex, descriptorIndex $descriptorOrSignatureIndex, index $index"

    override val length: Int
        get() = 10

}
