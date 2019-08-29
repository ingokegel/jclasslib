/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

/**
 * Base class for all constant pool entries in the constants package.
 */
abstract class Constant : Structure() {

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

    /**
     * Check if constant is equal to another one.
     */
    override fun equals(other: Any?): Boolean = other is Constant

    /**
     * Hash code of the constant,
     */
    override fun hashCode(): Int = 0

}
