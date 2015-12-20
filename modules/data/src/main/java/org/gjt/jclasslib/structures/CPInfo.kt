/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

import java.io.DataInput
import java.io.IOException

/**
 * Base class for all constant pool entries in the constants package.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com), [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
abstract class CPInfo : AbstractStructure() {

    /**
     * Type of the cp_info structure.
     */
    abstract val constantType: ConstantType

    /**
     * Verbose description of the content of the constant pool entry.
     */
    open val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = ""

    override fun equals(other: Any?): Boolean = other is CPInfo

    override fun hashCode(): Int = 0

    override fun printAccessFlagsVerbose(accessFlags: Int): String {
        if (accessFlags != 0)
            throw RuntimeException("Access flags should be zero: " + Integer.toHexString(accessFlags))
        return ""
    }

    companion object {

        /**
         * Factory method for creating CPInfo structures.
         *
         * A CPInfo of the appropriate subtype from the constants package
         * is created.
         *
         * @param input the DataInput from which to read the CPInfo structure
         * @param classFile the parent class file of the structure to be created
         * @return the new CPInfo structure
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        @JvmStatic
        fun create(input: DataInput, classFile: ClassFile): CPInfo {
            val constantType = ConstantType.getFromTag(input.readByte())
            val cpInfo: CPInfo = constantType.create()
            cpInfo.classFile = classFile
            cpInfo.read(input)

            return cpInfo
        }

        /**
         * Skip a CPInfo structure in a DataInput.
         * @param input the DataInput from which to read the CPInfo structure
         * @return the number of bytes skipped
         */
        @Throws(InvalidByteCodeException::class, IOException::class)
        @JvmStatic
        fun skip(input: DataInput): Int {

            val constantType = ConstantType.getFromTag(input.readByte())
            when (constantType) {

                ConstantType.UTF8 -> // Length of the constant is determined by the length of the byte array
                    input.skipBytes(input.readUnsignedShort())
                else -> input.skipBytes(constantType.size)
            }
            return constantType.extraEntryCount
        }
    }
}
