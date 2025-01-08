/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput

/**
 * Target info for a TypeAnnotation structure with empty content.
 */
class EmptyTargetInfo : TargetInfo() {

    override val length: Int
        get() = 0

    override val verbose: String
        get() = "<none>"

    override fun readData(input: DataInput) {
    }

    override fun writeData(output: DataOutput) {
    }

    override val debugInfo: String
        get() = ""
}
