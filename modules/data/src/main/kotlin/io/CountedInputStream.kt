/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io

import java.io.DataInputStream
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream

/**
 * InputStream which counts the number of bytes read.
 */
@Suppress("NOT_DOCUMENTED")
class CountedInputStream(input: InputStream) : FilterInputStream(input) {

    /**
     * Number of bytes read.
     */
    var bytesRead = 0
        private set

    @Throws(IOException::class)
    override fun read(): Int {
        val b = `in`.read()
        bytesRead++
        return b
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int = read(b, 0, b.size)

    @Throws(IOException::class)
    override fun read(b: ByteArray, offset: Int, len: Int): Int {
        val readCount = `in`.read(b, offset, len)
        bytesRead += readCount
        return readCount

    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        val skipCount = `in`.skip(n)
        bytesRead += skipCount.toInt()
        return skipCount
    }

    // Marking invalidates bytesRead
    override fun markSupported(): Boolean = false
}

@Suppress("NOT_DOCUMENTED")
class CountedDataInputStream(inputStream: InputStream) : DataInputStream(CountedInputStream(inputStream)) {
    val bytesRead : Int
        get() = (`in` as CountedInputStream).bytesRead
}

