/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io

import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.InvalidByteCodeException
import java.io.*
import java.util.jar.JarFile

/**
 * Converts class files to a class file structure ClassFile as defined in
 * org.gjt.jclasslib.structures.
 */
object ClassFileReader {

    /**
     * Looks up a class file in the specified class path and converts it
     * to a ClassFile structure.
     * @param classPath the class path from which to read the ClassFile structure
     * @param packageName the name of the package in which the class resides
     * @param className the simple name of the class
     * @return the new ClassFile structure or null if it cannot be found
     */
    @Throws(InvalidByteCodeException::class, IOException::class)
    @JvmStatic
    fun readFromClassPath(classPath: Array<String>, packageName: String, className: String): ClassFile? {

        val relativePath = packageName.replace('.', File.separatorChar) + (if (packageName.length == 0) "" else File.separator) + className + ".class"
        val jarRelativePath = relativePath.replace(File.separatorChar, '/')
        for (singlePath in classPath) {
            val currentClassPathEntry = File(singlePath)
            if (!currentClassPathEntry.exists()) {
                continue
            }
            if (currentClassPathEntry.isDirectory) {
                val testFile = File(currentClassPathEntry, relativePath)
                if (testFile.exists()) {
                    return readFromFile(testFile)
                }
            } else if (currentClassPathEntry.isFile) {
                JarFile(currentClassPathEntry).use { jarFile ->
                    val jarEntry = jarFile.getJarEntry(jarRelativePath)
                    if (jarEntry != null) {
                        return readFromInputStream(jarFile.getInputStream(jarEntry))
                    }
                }
            }
        }

        return null
    }

    /**
     * Converts a class file to a ClassFile structure.
     * @param file the file from which to read the ClassFile structure
     * @return the new ClassFile structure
     */
    @Throws(InvalidByteCodeException::class, IOException::class)
    @JvmStatic
    fun readFromFile(file: File): ClassFile {
        return readFromInputStream(FileInputStream(file))
    }

    /**
     * Converts a class file to a ClassFile structure.
     * @param input the input stream from which to read the ClassFile structure
     * @return the new ClassFile structure
     */
    @Throws(InvalidByteCodeException::class, IOException::class)
    @JvmStatic
    fun readFromInputStream(stream: InputStream): ClassFile {
        val classFile = ClassFile()
        DataInputStream(BufferedInputStream(stream)).use { classFile.read(it) }
        return classFile
    }

}
