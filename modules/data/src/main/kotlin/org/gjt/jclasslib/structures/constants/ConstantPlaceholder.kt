/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.ConstantType
import java.io.DataInput
import java.io.DataOutput

/**
 * Constant pool entry for unused indices in the constant pool, such as after [ConstantLongInfo] or [ConstantDoubleInfo]
 * entries.
 */
object ConstantPlaceholder : Constant() {

    override val constantType: ConstantType
        get() = throw UnsupportedOperationException()

    override fun readData(input: DataInput) {
        throw UnsupportedOperationException()
    }

    override fun writeData(output: DataOutput) {
        throw UnsupportedOperationException()
    }

    override val debugInfo: String
        get() = throw UnsupportedOperationException()
}