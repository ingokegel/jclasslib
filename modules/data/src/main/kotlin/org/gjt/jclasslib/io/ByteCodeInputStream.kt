/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io

import java.io.DataInputStream
import java.io.InputStream

/**
 * DataInputStream which extends ByteCodeInput.
 */
class ByteCodeInputStream(input: InputStream) : DataInputStream(CountedInputStream(input)), ByteCodeInput {

    override val bytesRead: Int
        get() = (`in` as CountedInputStream).bytesRead

}
