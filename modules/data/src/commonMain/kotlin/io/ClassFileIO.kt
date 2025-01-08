/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.io

import kotlinx.io.*
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.debug
import org.gjt.jclasslib.structures.isDebug

fun readFromPath(path: Path, suppressEOF: Boolean = false): ClassFile =
    readFromSource(SystemFileSystem.source(path), suppressEOF)

fun readFromSource(source: RawSource, suppressEOF: Boolean = false): ClassFile {
    val classFile = ClassFile()
    val input = source.createDataInput()
    if (suppressEOF) {
        try {
            classFile.read(input)
        } catch (e: EOFException) {
            if (isDebug) debug("A suppressed end-of-file occurred while reading the class file: ${e.message}", input)
        }
    } else {
        classFile.read(input)
    }
    return classFile
}

fun ClassFile.writeToPath(path: Path) = writeToSink(SystemFileSystem.sink(path))

fun ClassFile.writeToSink(sink: RawSink) {
    sink.createDataOutput().also {
        write(it)
        it.flush()
    }
}

fun ClassFile.writeToByteArray(): ByteArray =
    Buffer().let {
        writeToSink(it)
        it.readByteArray()
    }


private fun RawSource.createDataInput(): SourceDataInput {
    val source = buffered()
    return if (isDebug) {
        CountingSourceDataInput(source)
    } else {
        SourceDataInput(source)
    }
}

private fun RawSink.createDataOutput(): SinkDataOutput {
    val sink = this.buffered()
    return if (isDebug) {
        CountingSinkDataOutput(sink)
    } else {
        SinkDataOutput(sink)
    }
}
