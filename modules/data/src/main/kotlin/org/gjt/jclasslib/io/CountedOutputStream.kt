/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io

import java.io.FilterOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * OutputStream which counts the number of bytes written.
 */
class CountedOutputStream(out: OutputStream) : FilterOutputStream(out) {

    /**
     * Number of bytes written.
     */
    var bytesWritten = 0
        private set

    @Throws(IOException::class)
    override fun write(b: Int) {
        out.write(b)
        bytesWritten++
    }
}
