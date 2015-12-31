/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import java.io.DataInput
import java.io.DataOutput

class DefaultVerificationTypeEntry(verificationType : VerificationType) : VerificationTypeInfoEntry(verificationType) {

    override fun write(output: DataOutput) {
        super.write(output)
        debugWrite()
    }

    override fun read(input: DataInput) {
        super.read(input)
        debugRead()
    }
}