/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.test

import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.io.ClassFileWriter
import org.gjt.jclasslib.structures.isDebug
import org.testng.annotations.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.jar.JarEntry
import java.util.jar.JarFile

class Tests {
    @Test
    fun testCurrentJre() {
        scanJar(File("${System.getProperty("java.home")}/lib/rt.jar"))
    }

    @Test
    fun test() {
        checkClassFile("Main", InputStreamProvider::class.java.getResource("/java8/Main.class"))
    }
}

fun scanJar(file: File) {
    val jar = JarFile(file)
    var count = 0
    var errors = 0
    jar.entries().iterator().forEach { entry ->
        val name = entry.name
        if (name.endsWith(".class")) {
            val className = name.removeSuffix(".class").replace("/", ".")
            try {
                if (!checkClassFile(className, JarInputStreamProvider(jar, entry))) {
                    errors++
                }
            } catch (e: Throwable) {
                error(className)
                throw e
            }
            count++
        }
    }
    println(count.toString() + " classes checked, " + errors + " errors")
}

interface InputStreamProvider {
    fun createInputStream(): InputStream
}

class JarInputStreamProvider(private val jarFile: JarFile, private val jarEntry: JarEntry) : InputStreamProvider {
    override fun createInputStream() = jarFile.getInputStream(jarEntry)
}

class UrlInputStreamProvider(private val url: URL) : InputStreamProvider {
    override fun createInputStream() = url.openConnection().inputStream
}

fun checkClassFile(className: String, url: URL): Boolean {
    return checkClassFile(className, UrlInputStreamProvider(url))
}

fun checkClassFile(className: String, inputStreamProvider: InputStreamProvider): Boolean {
    val output = ByteArrayOutputStream()
    inputStreamProvider.createInputStream().use {
        it.copyTo(output)
    }
    val before = output.toByteArray()

    val classFile = ClassFileReader.readFromInputStream(inputStreamProvider.createInputStream())
    val after = ClassFileWriter.writeToByteArray(classFile)

    val success = compare(className, before, after)
    if (!success) {
        isDebug = true
        ClassFileReader.readFromInputStream(ByteArrayInputStream(after))
    }
    return success
}

fun error(className: String) {
    System.err.println("ERROR when processing " + className)
}

fun compare(className: String, before: ByteArray, after: ByteArray): Boolean {
    if (before.size != after.size) {
        System.err.println("ERROR in " + className)
        System.err.println("Different length " + before.size + " != " + after.size)
        return false
    }
    for (i in before.indices) {
        if (before[i] != after[i]) {
            System.err.println("Different byte at index " + i)
            return false
        }
    }
    return true
}

