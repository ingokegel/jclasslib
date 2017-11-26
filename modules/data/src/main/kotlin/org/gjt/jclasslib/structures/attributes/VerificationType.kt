/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.ClassFileEnum
import org.gjt.jclasslib.structures.Lookup

/**
 * Represents the verification type of a bootstrap entry.
 */

@Suppress("NOT_DOCUMENTED")
enum class VerificationType(override val tag: Int) : ClassFileEnum {
    TOP(0),
    INTEGER(1),
    FLOAT(2),
    DOUBLE(3),
    LONG(4),
    ITEM_NULL(5),
    NULL(6),
    OBJECT(7),
    UNINITIALIZED(8);

    /**
     * Create an associated [VerificationTypeInfoEntry] instance.
     */
    fun createEntry(classFile: ClassFile): VerificationTypeInfoEntry = when (this) {
        OBJECT -> ObjectVerificationTypeInfoEntry(classFile)
        UNINITIALIZED -> UninitializedVerificationTypeInfoEntry()
        else -> VerificationTypeInfoEntry(this)
    }

    companion object : Lookup<VerificationType>(VerificationType::class.java, "verification tag")
}
