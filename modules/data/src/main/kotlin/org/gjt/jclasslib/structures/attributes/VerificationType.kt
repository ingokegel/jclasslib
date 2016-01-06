/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException

/**
 * Represents the verification type of a bootstrap entry.
 */

@Suppress("NOT_DOCUMENTED")
enum class VerificationType(val tag: Int) {

    TOP(0),
    INTEGER(1),
    FLOAT(2),
    DOUBLE(3),
    LONG(4),
    ITEM_Null(5),
    NULL(6),
    OBJECT(7),
    UNINITIALIZED(8);

    /**
     * Create an associated [VerificationTypeInfoEntry] instance.
     */
    fun createEntry(classFile: ClassFile): VerificationTypeInfoEntry {
        when (this) {
            OBJECT -> return ObjectVerificationTypeInfoEntry(classFile)
            UNINITIALIZED -> return UninitializedVerificationTypeInfoEntry()
            else -> return VerificationTypeInfoEntry(this)
        }
    }

    companion object {
        @Throws(InvalidByteCodeException::class)
        fun getFromTag(tag: Int): VerificationType {
            val values = values()
            if (tag < 0 || tag >= values.size) {
                throw InvalidByteCodeException("Invalid verification tag " + tag)
            }
            return values[tag]
        }
    }
}
