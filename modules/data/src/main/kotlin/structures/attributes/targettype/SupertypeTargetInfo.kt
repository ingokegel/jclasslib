/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import java.io.DataInput
import java.io.DataOutput

/**
 * Target info for a TypeAnnotation structure with a super class target.
 */
class SupertypeTargetInfo : TargetInfo() {

    /**
     * The index of the super type, 65535 if the super class is the target, otherwise
     * an index in the list of implemented interfaces.
     */
    var supertypeIndex: Int = 0

    override fun readData(input: DataInput) {
        supertypeIndex = input.readUnsignedShort()
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(supertypeIndex)
    }

    override val length: Int
        get() = 2

    override val verbose: String
        get() {
            return if (supertypeIndex == 65535) {
                "Super class ($supertypeIndex)"
            } else {
                "<a href=\"I$supertypeIndex\">interface index $supertypeIndex</a>"
            }
        }

    override val debugInfo: String
        get() = "with supertypeIndex $supertypeIndex"
}
