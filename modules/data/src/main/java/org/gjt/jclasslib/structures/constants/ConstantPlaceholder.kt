/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.CPInfo
import org.gjt.jclasslib.structures.ConstantType
import java.io.DataInput
import java.io.DataOutput

object ConstantPlaceholder : CPInfo() {

    override val constantType: ConstantType
        get() = throw UnsupportedOperationException()

    override fun read(input: DataInput) {
        throw UnsupportedOperationException()
    }

    override fun write(output: DataOutput) {
        throw UnsupportedOperationException()
    }
}