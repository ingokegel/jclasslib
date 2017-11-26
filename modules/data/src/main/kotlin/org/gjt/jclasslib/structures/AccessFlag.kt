/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

import java.util.*

/**
 * Defines access flags constants and verbose expressions as defined by
 * the java access modifiers.
 * @property flag the flag
 * @property verbose Verbose form of the flag suitable for printing a list of access flags
 */
@Suppress("NOT_DOCUMENTED")
enum class AccessFlag(val flag: Int, val verbose: String) {
    PUBLIC(0x0001, "public"),
    PRIVATE(0x0002, "private"),
    PROTECTED(0x0004, "protected"),
    STATIC(0x0008, "static"),
    FINAL(0x0010, "final"),
    SYNCHRONIZED(0x0020, "synchronized"),
    /**
     * For ClassFile structures, 0x0020 is ACC_SUPER, which has historical significance only
     */
    SUPER(0x0020, ""),
    VOLATILE(0x0040, "volatile"),
    TRANSIENT(0x0080, "transient"),

    NATIVE(0x0100, "native"),
    INTERFACE(0x0200, "interface"),
    ABSTRACT(0x0400, "abstract"),
    STRICT(0x0800, "strict"),

    /**
     * new in Java 1.4
     */
    SYNTHETIC(0x1000, "synthetic"),
    /**
     * new in Java 1.5
     */
    ANNOTATION(0x2000, "annotation"),
    /**
     * new in Java 1.5
     */
    ENUM(0x4000, "enum"),
    /**
     * new in Java 1.5
     */
    BRIDGE(0x0040, "bridge"),
    /**
     * new in Java 1.5
     */
    VARARGS(0x0080, "varargs"),
    /**
     * new in Java 8
     */
    MANDATED(0x8000, "mandated"),
    /**
     * new in Java 9
     */
    MODULE(0x8000, "module"),
    /**
     * new in Java 9
     */
    OPEN(0x0020, "open"),
    /**
     * new in Java 9
     */
    TRANSITIVE(0x0020, "transitive"),
    /**
     * new in Java 9
     */
    STATIC_PHASE(0x0040, "static");

    /** Checks if this access flag is set in the supplied access flags.
     * @param accessFlags the access flags
     */
    fun isSet(accessFlags: Int): Boolean = accessFlags and flag == flag

    override fun toString() = verbose

    companion object {

        //TODO pass enum set with acceptable values
        fun decompose(accessFlags: Int): List<AccessFlag> = values().filter { it.isSet(accessFlags) }

        /**
         * Class access flags
         */
        val CLASS_ACCESS_FLAGS = enumSet(
                PUBLIC,
                FINAL,
                SUPER,
                INTERFACE,
                ABSTRACT,
                SYNTHETIC,
                ANNOTATION,
                ENUM,
                MODULE)

        /**
         * Inner class access flags
         */
        val INNER_CLASS_ACCESS_FLAGS = enumSet(
                PUBLIC,
                PRIVATE,
                PROTECTED,
                STATIC,
                FINAL,
                INTERFACE,
                ABSTRACT,
                SYNTHETIC,
                ANNOTATION,
                ENUM)

        /**
         * Field access flags
         */
        val FIELD_ACCESS_FLAGS = enumSet(
                PUBLIC,
                PRIVATE,
                PROTECTED,
                STATIC,
                FINAL,
                VOLATILE,
                TRANSIENT,
                SYNTHETIC,
                ENUM)

        /**
         * Method access flags
         */
        val METHOD_ACCESS_FLAGS = enumSet(
                PUBLIC,
                PRIVATE,
                PROTECTED,
                STATIC,
                FINAL,
                SYNCHRONIZED,
                BRIDGE,
                VARARGS,
                NATIVE,
                ABSTRACT,
                STRICT,
                SYNTHETIC)

        /**
         * Access flags for MethodParameters attribute
         */
        val METHOD_PARAMETERS_ACCESS_FLAGS = enumSet(
                FINAL,
                SYNTHETIC,
                MANDATED
        )

        /**
         * Access flags for Module attribute
         */
        val MODULE_FLAGS = enumSet(
                OPEN,
                SYNTHETIC,
                MANDATED
        )

        /**
         * Access flags for requires entry in the Module attribute
         */
        val REQUIRES_FLAGS = enumSet(
                TRANSITIVE,
                STATIC_PHASE,
                SYNTHETIC,
                MANDATED
        )
        /**
         * Access flags for exports entry in the Module attribute
         */
        val EXPORTS_FLAGS = enumSet(
                SYNTHETIC,
                MANDATED
        )

        private fun enumSet(accessFlag: AccessFlag, vararg accessFlags: AccessFlag) : EnumSet<AccessFlag> =
                EnumSet.of(accessFlag, *accessFlags)
    }
}
