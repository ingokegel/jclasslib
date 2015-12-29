/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AbstractStructure
import org.gjt.jclasslib.structures.AccessFlag
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an entry in a InnerClasses attribute structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com), [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class InnerClassesEntry : AbstractStructure() {

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
        get() = printAccessFlags(innerClassAccessFlags)

    /**
     * Verbose description of the access flags of the inner class.
     */
    val innerClassAccessFlagsVerbose: String
        get() = printAccessFlagsVerbose(AccessFlag.INNER_CLASS_ACCESS_FLAGS, innerClassAccessFlags)

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        innerClassInfoIndex = input.readUnsignedShort()
        outerClassInfoIndex = input.readUnsignedShort()
        innerNameIndex = input.readUnsignedShort()
        innerClassAccessFlags = input.readUnsignedShort()

        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(innerClassInfoIndex)
        output.writeShort(outerClassInfoIndex)
        output.writeShort(innerNameIndex)
        output.writeShort(innerClassAccessFlags)

        if (isDebug) debug("wrote")
    }

    override fun debug(message: String) {
        super.debug("$message InnerClasses entry with inner_class_info_index $innerClassInfoIndex, " +
                "outer_class_info_index $outerClassInfoIndex, inner_name_index $innerNameIndex, " +
                "access flags ${printAccessFlags(innerClassAccessFlags)}")
    }

    companion object {

        /**
         * Length in bytes of an inner class entry.
         */
        val LENGTH = 8

        /**
         * Factory method for creating InnerClassesEntry structures.
         * @param input the DataInput from which to read the InnerClassesEntry structure
         * @param classFile the parent class file of the structure to be created
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        fun create(input: DataInput, classFile: ClassFile) = InnerClassesEntry().apply {
            this.classFile = classFile
            this.read(input)
        }
    }

}
