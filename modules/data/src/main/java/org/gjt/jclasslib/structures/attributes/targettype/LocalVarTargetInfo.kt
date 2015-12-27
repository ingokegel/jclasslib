/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import org.gjt.jclasslib.structures.InvalidByteCodeException

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Target info for a TypeAnnotation structure with local variable table links.
 */
class LocalVarTargetInfo : TargetInfo() {

    var localVarTargets: Array<LocalVarTarget> = emptyArray()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        val count = input.readUnsignedShort()
        localVarTargets = Array(count) {
            LocalVarTarget().apply { read(input) }
        }
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeShort(localVarTargets.size)
        localVarTargets.forEach { it.write(output) }
    }

    override val length: Int
        get() = 2 + localVarTargets.size * 6

    override val verbose: String
        get() {
            val buffer = StringBuilder()
            localVarTargets.forEachIndexed { i, localVarTarget ->
                buffer.append("[").append(i).append("] start: ").append(localVarTarget.startPc)
                buffer.append(", length: ").append(localVarTarget.length)
                buffer.append(", <a href=\"L").append(localVarTarget.index).append("\">local variable with index ").append(localVarTarget.index).append("</a>")
                buffer.append("\n")
            }
            return buffer.toString()
        }
}
