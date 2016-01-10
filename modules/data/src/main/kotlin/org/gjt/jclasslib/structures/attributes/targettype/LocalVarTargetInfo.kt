/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import org.gjt.jclasslib.structures.emptyArraySingleton
import java.io.DataInput
import java.io.DataOutput

/**
 * Target info for a TypeAnnotation structure with local variable table links.
 */
class LocalVarTargetInfo : TargetInfo() {

    /**
     * Contained local variable targets.
     */
    var localVarTargets: Array<LocalVarTarget> = emptyArraySingleton()

    override fun readData(input: DataInput) {
        val count = input.readUnsignedShort()
        localVarTargets = Array(count) {
            LocalVarTarget().apply { read(input) }
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(localVarTargets.size)
        localVarTargets.forEach { it.write(output) }
    }

    override val length: Int
        get() = 2 + localVarTargets.sumBy { it.length }

    override val verbose: String
        get() {
            val buffer = StringBuilder()
            localVarTargets.forEachIndexed { i, localVarTarget ->
                buffer.append("[").append(i).append("] start: ").append(localVarTarget.startPc)
                buffer.append(", length: ").append(localVarTarget.targetLength)
                buffer.append(", <a href=\"L").append(localVarTarget.index).append("\">local variable with index ").append(localVarTarget.index).append("</a>")
                buffer.append("\n")
            }
            return buffer.toString()
        }

    override val debugInfo: String
        get() = "with ${localVarTargets.size} targets"
}
