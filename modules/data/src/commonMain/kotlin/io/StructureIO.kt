/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.io

import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import org.gjt.jclasslib.structures.readUnsignedByte
import org.gjt.jclasslib.structures.readUnsignedShort
import org.gjt.jclasslib.structures.writeByte
import org.gjt.jclasslib.structures.writeShort

interface DataInput {
    /**
     * Read an unsigned byte.
     * @return the byte value
     */
    fun readUnsignedByte(): Int

    /**
     * Read a byte.
     * @return the byte value
     */
    fun readByte(): Byte

    /**
     * Read an unsigned short.
     * @return the short value
     */
    fun readUnsignedShort(): Int

    /**
     * Read a short.
     * @return the short value
     */
    fun readShort(): Short

    /**
     * Read an int.
     * @return the int value
     */
    fun readInt(): Int

    /**
     * Read a byte array with the given number of bytes.
     * @param length the number of bytes to read
     */
    fun readByteArray(length: Int): ByteArray

    /**
     * Skip a number of bytes
     * @param count the number of bytes to skip
     */
    fun skip(count: Int)
}

interface CountingDataInput : DataInput {
    /**
     * Get the number of bytes read.
     */
    val bytesRead: Int
}

/**
 * Exposes methods from Source and adds a property for the number of bytes written.
 */
interface DataOutput {
    /**
     * Write a byte.
     * @param value the short value
     */
    fun writeByte(value: Int)

    /**
     * Write a short.
     * @param value the short value
     */
    fun writeShort(value: Int)

    /**
     * Write an int.
     * @param value the int value
     */
    fun writeInt(value: Int)

    /**
     * Write a byte array
     * @param bytes the byte array to be written
     */
    fun write(bytes: ByteArray)
}

interface CountingDataOutput : DataOutput {
    /**
     * Get the number of bytes written.
     */
    val bytesWritten: Int
}

internal open class SourceDataInput(private val source: Source) : DataInput {
    override fun readUnsignedByte(): Int = source.readUnsignedByte()
    override fun readByte(): Byte = source.readByte()
    override fun readUnsignedShort(): Int = source.readUnsignedShort()
    override fun readShort(): Short = source.readShort()
    override fun readInt(): Int = source.readInt()
    override fun readByteArray(length: Int): ByteArray = source.readByteArray(length)

    override fun skip(count: Int) {
        source.skip(count.toLong())
    }
}

internal open class CountingSourceDataInput(source: Source) : SourceDataInput(source), CountingDataInput {
    constructor(byteArray: ByteArray) : this(Buffer().also { it.write(byteArray) })

    private var _bytesRead: Int = 0
    override val bytesRead: Int get() = _bytesRead

    override fun readUnsignedByte(): Int = super.readUnsignedByte().also { _bytesRead++ }
    override fun readByte(): Byte = super.readByte().also { _bytesRead++ }
    override fun readUnsignedShort(): Int = super.readUnsignedShort().also { _bytesRead += 2 }
    override fun readShort(): Short = super.readShort().also { _bytesRead += 2 }
    override fun readInt(): Int = super.readInt().also { _bytesRead += 4 }
    override fun readByteArray(length: Int): ByteArray = super.readByteArray(length).also { _bytesRead += length }

    override fun skip(count: Int) {
        super.skip(count).also { _bytesRead += count }
    }
}

internal open class SinkDataOutput(private val sink: Sink) : DataOutput {
    override fun writeByte(value: Int) {
        sink.writeByte(value)
    }

    override fun writeShort(value: Int) {
        sink.writeShort(value)
    }

    override fun writeInt(value: Int) {
        sink.writeInt(value)
    }

    override fun write(bytes: ByteArray) {
        sink.write(bytes)
    }

    fun flush() {
        sink.flush()
    }
}

internal open class CountingSinkDataOutput(sink: Sink) : SinkDataOutput(sink), CountingDataOutput {
    private var _bytesWritten: Int = 0
    override val bytesWritten: Int get() = _bytesWritten

    override fun writeByte(value: Int) {
        super.writeByte(value).also { _bytesWritten++ }
    }

    override fun writeShort(value: Int) {
        super.writeShort(value).also { _bytesWritten += 2 }
    }

    override fun writeInt(value: Int) {
        super.writeInt(value).also { _bytesWritten += 4 }
    }

    override fun write(bytes: ByteArray) {
        super.write(bytes).also { _bytesWritten += bytes.size }
    }
}

internal open class BufferedDataOutput(private val buffer: Buffer = Buffer()) : CountingSinkDataOutput(buffer) {
    fun toByteArray(): ByteArray = buffer.readByteArray()
}
