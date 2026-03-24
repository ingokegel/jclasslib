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
import org.gjt.jclasslib.getSystemProperty
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.SYSTEM_PROPERTY_SKIP_ATTRIBUTES
import org.gjt.jclasslib.structures.debug
import org.gjt.jclasslib.structures.isDebug

fun readFromPath(path: Path, suppressEOF: Boolean = false, readMode: ClassFileReadMode = ClassFileReadMode.FULL): ClassFile =
    readFromSource(SystemFileSystem.source(path), suppressEOF, readMode)

fun readFromSource(source: RawSource, suppressEOF: Boolean = false, readMode: ClassFileReadMode = ClassFileReadMode.FULL): ClassFile {
    val classFile = ClassFile()
    val resolvedReadMode = if (readMode != ClassFileReadMode.FULL) {
        readMode
    } else if (getSystemProperty(SYSTEM_PROPERTY_SKIP_ATTRIBUTES) == "true") {
        ClassFileReadMode.SKIP_ATTRIBUTES
    } else {
        ClassFileReadMode.FULL
    }
    val input = source.createDataInput(resolvedReadMode)
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


private fun RawSource.createDataInput(readMode: ClassFileReadMode = ClassFileReadMode.FULL): SourceDataInput {
    val source = buffered()
    return if (isDebug) {
        CountingSourceDataInput(source, readMode)
    } else {
        SourceDataInput(source, readMode)
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
