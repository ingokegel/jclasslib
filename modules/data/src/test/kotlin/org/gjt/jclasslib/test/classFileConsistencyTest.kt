/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.test

import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.io.ClassFileWriter
import org.gjt.jclasslib.io.forEachClassInJrt
import org.gjt.jclasslib.io.getJrtInputStream
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
        scanJre(System.getProperty("java.home"))
    }

    @Test
    fun test() {
        checkClassFile("Main", InputStreamProvider::class.java.getResource("/Main.class"))
    }
}

fun scanJre(javaHome: String) {
    val testStatistics = TestStatistics()
    val rtJar = File("$javaHome/lib/rt.jar")
    if (rtJar.exists()) {
        scanJar(rtJar, testStatistics)
    } else {
        scanJrt(File(javaHome), testStatistics)
    }
    println("${testStatistics.count} classes checked, ${testStatistics.errors} errors")
}

fun scanJar(file: File, testStatistics: TestStatistics) {
    val jar = JarFile(file)
    jar.entries().iterator().forEach { entry ->
        val fileName = entry.name
        if (fileName.endsWith(".class")) {
            checkClassFile(fileName, JarInputStreamProvider(jar, entry), testStatistics)
        }
    }
}

fun scanJrt(jreHome: File, testStatistics: TestStatistics) {
    forEachClassInJrt(jreHome) { path ->
        val fileName = path.toString()
        checkClassFile(fileName, JrtInputStreamProvider(fileName, jreHome), testStatistics)
    }
}

data class TestStatistics(var count : Int = 0, var errors : Int = 0)

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

private fun checkClassFile(fileName: String, inputStreamProvider: InputStreamProvider, testStatistics: TestStatistics) {
    val className = fileName.removeSuffix(".class").replace("/", ".")
    try {
        if (!checkClassFile(className, inputStreamProvider)) {
            testStatistics.errors++
        }
    } catch (e: Throwable) {
        error(className)
        throw e
    }
    testStatistics.count++
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
        isDebug = false
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

