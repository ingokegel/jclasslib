/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import org.gjt.jclasslib.structures.AbstractConstant
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.ConstantType

/**
 * Describes a CONSTANT_Utf8_info constant pool data structure.
 */
class ConstantUtf8Info(classFile: ClassFile) : AbstractConstant(classFile) {

    private var rawBytes: ByteArray? = null
    private var decodedString: String? = null

    /**
     * The string in this entry. Decoded lazily from the raw bytes on first access.
     */
    var string: String
        get() {
            decodedString?.let { return it }
            return decodeUTF(rawBytes ?: return "").also { decodedString = it }
        }
        set(value) {
            decodedString = value
            rawBytes = null
        }

    override val constantType: ConstantType
        get() = ConstantType.UTF8

    override val verbose: String
        get() = string

    /**
     * The byte array of the string in this entry.
     * *
     */
    val bytes: ByteArray
        get() = string.encodeToByteArray()

    override fun readData(input: DataInput) {
        val utfLen = input.readUnsignedShort()
        rawBytes = input.readByteArray(utfLen)
        decodedString = null
    }

    override fun writeData(output: DataOutput) {
        output.writeByte(ConstantType.UTF8.tag)
        output.writeUTF(string)
    }

    override val debugInfo: String
        get() = "with length ${string.length} (\"$string\")"

    override fun equals(other: Any?): Boolean {
        if (other !is ConstantUtf8Info) {
            return false
        }
        return super.equals(other) && other.string == string
    }

    override fun hashCode(): Int = super.hashCode() xor string.hashCode()

    private fun DataOutput.writeUTF(str: String): Int {
        val strlen = str.length
        var utfLen = strlen

        for (i in 0 until strlen) {
            val c = str[i].code
            if (c >= 0x80 || c == 0) {
                utfLen += if (c >= 0x800) 2 else 1
            }
        }

        if (utfLen !in strlen..65535) {
            throw IllegalStateException("String too long: $utfLen chars")
        }

        val bytes = ByteArray(utfLen + 2)

        var count = 0
        bytes[count++] = (utfLen ushr 8 and 0xFF).toByte()
        bytes[count++] = (utfLen and 0xFF).toByte()

        var i = 0
        while (i < strlen) {
            val c = str[i].code
            if (c >= 0x80 || c == 0) break
            bytes[count++] = c.toByte()
            i++
        }

        while (i < strlen) {
            val c = str[i].code
            when {
                c < 0x80 && c != 0 -> {
                    bytes[count++] = c.toByte()
                }

                c >= 0x800 -> {
                    bytes[count++] = (0xE0 or (c shr 12 and 0x0F)).toByte()
                    bytes[count++] = (0x80 or (c shr 6 and 0x3F)).toByte()
                    bytes[count++] = (0x80 or (c and 0x3F)).toByte()
                }

                else -> {
                    bytes[count++] = (0xC0 or (c shr 6 and 0x1F)).toByte()
                    bytes[count++] = (0x80 or (c and 0x3F)).toByte()
                }
            }
            i++
        }

        write(bytes)
        return utfLen + 2
    }

    private fun decodeUTF(bytes: ByteArray): String {
        val utfLen = bytes.size

        // Fast path: scan for first non-ASCII byte
        var count = 0
        while (count < utfLen) {
            if (bytes[count].toInt() and 0xFF > 127) break
            count++
        }

        // Pure ASCII: skip CharArray allocation, use efficient platform String construction
        if (count == utfLen) {
            return bytes.decodeToString()
        }

        // Multi-byte path: allocate CharArray only when needed
        val chars = CharArray(utfLen)
        var charCount = count
        for (i in 0..count) {
            chars[i] = bytes[i].toInt().toChar()
        }

        while (count < utfLen) {
            val c = bytes[count].toInt() and 0xFF
            when (c shr 4) {
                in 0..7 -> {
                    count++
                    chars[charCount++] = c.toChar()
                }
                12, 13 -> {
                    count += 2
                    if (count > utfLen) {
                        throw IllegalStateException("malformed input: partial character at end")
                    }
                    val char2 = bytes[count - 1].toInt()
                    if ((char2 and 0xC0) != 0x80) {
                        throw IllegalStateException("malformed input around byte $count")
                    }
                    chars[charCount++] = (((c and 0x1F) shl 6) or (char2 and 0x3F)).toChar()
                }
                14 -> {
                    count += 3
                    if (count > utfLen) {
                        throw IllegalStateException("malformed input: partial character at end")
                    }
                    val char2 = bytes[count - 2].toInt()
                    val char3 = bytes[count - 1].toInt()
                    if ((char2 and 0xC0) != 0x80 || (char3 and 0xC0) != 0x80) {
                        throw IllegalStateException("malformed input around byte ${count - 1}")
                    }
                    chars[charCount++] =
                        (((c and 0x0F) shl 12) or ((char2 and 0x3F) shl 6) or (char3 and 0x3F)).toChar()
                }
                else -> {
                    throw IllegalStateException("malformed input around byte $count")
                }
            }
        }
        return chars.concatToString(0, charCount)
    }
}
