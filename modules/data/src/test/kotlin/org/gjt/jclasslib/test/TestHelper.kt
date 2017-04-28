/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.test

import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.io.getJrtInputStream
import org.gjt.jclasslib.structures.ClassFile
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.jar.JarEntry
import java.util.jar.JarFile

interface InputStreamProvider {
    fun createInputStream(): InputStream
}

class JarInputStreamProvider(private val jarFile: JarFile, private val jarEntry: JarEntry) : InputStreamProvider {
    override fun createInputStream(): InputStream = jarFile.getInputStream(jarEntry)
}

class UrlInputStreamProvider(private val url: URL) : InputStreamProvider {
    override fun createInputStream(): InputStream = url.openConnection().inputStream
}

class JrtInputStreamProvider(private val fileName: String, private val jreHome : File) : InputStreamProvider {
    override fun createInputStream() = getJrtInputStream(fileName, jreHome)
}

fun readClassFile(url: URL): ClassFile {
    return ClassFileReader.readFromInputStream(UrlInputStreamProvider(url).createInputStream())
}

fun readClassFile(resourcePath: String): ClassFile {
    return readClassFile(InputStreamProvider::class.java.getResource(resourcePath))
}

