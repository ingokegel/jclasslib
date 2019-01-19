/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io

import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.isDebug

import java.io.*

/**
 * Converts class file structure ClassFile as defined in
 * org.gjt.jclasslib.structures to class files.
 */
object ClassFileWriter {

    /**
     * Converts a ClassFile structure to a class file.
     * @param file the file to which to write the ClassFile structure
     * @param classFile the ClassFile structure to be written
     */
    @Throws(InvalidByteCodeException::class, IOException::class)
    @JvmStatic
    fun writeToFile(file: File, classFile: ClassFile) {
        DataOutputStream(BufferedOutputStream(FileOutputStream(file)).wrapForDebug()).use { classFile.write(it) }
    }

    /**
     * Converts a ClassFile structure to a byte array.
     * @param classFile the class file
     */
    @Throws(InvalidByteCodeException::class, IOException::class)
    @JvmStatic
    fun writeToByteArray(classFile: ClassFile): ByteArray {
        val result = ByteArrayOutputStream()
        result.wrapForDebug().use { classFile.write(it) }
        return result.toByteArray()
    }

    private fun OutputStream.wrapForDebug() = if (isDebug) CountedDataOutputStream(this) else DataOutputStream(this)

}
