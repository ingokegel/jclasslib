/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import java.io.DataInput
import java.io.DataOutput

object UndefinedTargetInfo : TargetInfo() {

    override val length: Int
        get() = throw UnsupportedOperationException()

    override val verbose: String
        get() = throw UnsupportedOperationException()

    override fun read(input: DataInput) {
        throw UnsupportedOperationException()
    }

    override fun write(output: DataOutput) {
        throw UnsupportedOperationException()
    }

}