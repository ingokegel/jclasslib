/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.CPInfo
import org.gjt.jclasslib.structures.ConstantType
import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes a CONSTANT_Utf8_info constant pool data structure.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */
class ConstantUtf8Info : CPInfo() {

    /**
     * The string in this entry.
     */
    var string: String = ""

    override val constantType: ConstantType
        get() = ConstantType.UTF8

    override val verbose: String
        @Throws(InvalidByteCodeException::class)
        get() = string

    /**
     * The byte array of the string in this entry.
     * *
     */
    val bytes: ByteArray
        get() = string.toByteArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        string = input.readUTF()
        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(ConstantType.UTF8.tag)
        output.writeUTF(string)
        if (isDebug) debug("wrote")
    }

    override fun debug(message: String) {
        super.debug("$message $constantType with length ${string.length} (\"$string\")")
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantUtf8Info) {
            return false
        }
        return super.equals(other) && other.string == string
    }

    override fun hashCode(): Int = super.hashCode() xor string.hashCode()

}
