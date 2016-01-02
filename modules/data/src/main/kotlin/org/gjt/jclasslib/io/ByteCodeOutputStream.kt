/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io

import java.io.DataOutputStream
import java.io.OutputStream

/**
 * DataOutputStream which extends ByteCodeOutput.
 */
class ByteCodeOutputStream(output: OutputStream) : DataOutputStream(CountedOutputStream(output)), ByteCodeOutput {

    override val bytesWritten: Int
        get() = (out as CountedOutputStream).bytesWritten
}
