/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AccessFlag
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes an entry in a InnerClasses attribute structure.
 */
class InnerClassesEntry : SubStructure() {

    /**
     * Constant pool index of the CONSTANT_Class_info structure
     * describing the inner class of this InnerClassEntry.
     */
    var innerClassInfoIndex: Int = 0

    /**
     * Constant pool index of the CONSTANT_Class_info structure
     * describing the outer class of this InnerClassEntry.
     */
    var outerClassInfoIndex: Int = 0

    /**
     * Constant pool index containing the simple name of the
     * inner class of this InnerClassEntry.
     */
    var innerNameIndex: Int = 0

    /**
     * Access flags of the inner class.
     */
    var innerClassAccessFlags: Int = 0

    /**
     * Access flags of the inner class as a hex string.
     */
    val innerClassFormattedAccessFlags: String
        get() = formatAccessFlags(innerClassAccessFlags)

    /**
     * Verbose description of the access flags of the inner class.
     */
    val innerClassAccessFlagsVerbose: String
        get() = formatAccessFlagsVerbose(AccessFlag.INNER_CLASS_ACCESS_FLAGS, innerClassAccessFlags)

    override fun readData(input: DataInput) {
        innerClassInfoIndex = input.readUnsignedShort()
        outerClassInfoIndex = input.readUnsignedShort()
        innerNameIndex = input.readUnsignedShort()
        innerClassAccessFlags = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(innerClassInfoIndex)
        output.writeShort(outerClassInfoIndex)
        output.writeShort(innerNameIndex)
        output.writeShort(innerClassAccessFlags)
    }

    override val debugInfo: String
        get() = "with innerClassInfoIndex $innerClassInfoIndex, " +
                "outerClassInfoIndex $outerClassInfoIndex, innerNameIndex $innerNameIndex, " +
                "accessFlags ${formatAccessFlags(innerClassAccessFlags)}"

    override val length: Int
        get() = 8

}
