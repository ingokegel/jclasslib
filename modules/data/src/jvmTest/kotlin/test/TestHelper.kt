/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.test

import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.io.getJrtInputStream
import org.gjt.jclasslib.structures.ClassFile
import java.io.File
import java.io.FileInputStream
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

class FileInputStreamProvider(private val file: File) : InputStreamProvider {
    override fun createInputStream(): InputStream = FileInputStream(file).buffered()
}

class JrtInputStreamProvider(private val fileName: String, private val jreHome: File) : InputStreamProvider {
    override fun createInputStream() = getJrtInputStream(fileName, jreHome)
}

fun readResourceClassFile(url: URL): ClassFile {
    return ClassFileReader.readFromInputStream(UrlInputStreamProvider(url).createInputStream())
}

fun readResourceClassFile(resourcePath: String): ClassFile {
    return readResourceClassFile(getResourceClassFileUrl(resourcePath))
}

fun getResourceClassFileUrl(resourcePath: String): URL =
    requireNotNull(InputStreamProvider::class.java.getResource(resourcePath))

fun createResourceClassFileInputStreamProvider(resourcePath: String): InputStreamProvider =
    UrlInputStreamProvider(getResourceClassFileUrl(resourcePath))

fun createJavaClassFileInputStreamProvider(path: String): InputStreamProvider {
    val javaClassesRoot = File(getResourceClassFileUrl("/").toURI()).parentFile.resolve("java")
    return FileInputStreamProvider(javaClassesRoot.resolve(path))
}

