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
import org.gjt.jclasslib.structures.isDebug
import org.testng.annotations.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.nio.file.Path
import java.util.jar.JarFile

class ClassFileConsistencyTest {
    @Test
    fun testCurrentJre() {
        scanJre(System.getProperty("java.home"))
        //scanJre("C:\\Program Files\\Java\\jdk-9")
    }

    @Test
    fun testCurrentJreModules() {
        scanJreModules(System.getProperty("java.home"))
        //scanJreModules("C:\\Program Files\\Java\\jdk-9", true)
    }

    @Test
    fun testSingleClasses() {
        checkClassFile("/Main.class")
        checkClassFile("/moduleMainClass/module-info.class")
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

    fun scanJreModules(javaHome: String, logPaths: Boolean = false) {
        val rtJar = File("$javaHome/lib/rt.jar")
        if (rtJar.exists()) {
            println("Not a modular JRE")
            return
        }
        val testStatistics = TestStatistics()
        scanJrt(File(javaHome), testStatistics, logPaths) { path ->
            path.endsWith("module-info.class")
        }
        println("${testStatistics.count} modules checked, ${testStatistics.errors} errors")
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

    fun scanJrt(jreHome: File, testStatistics: TestStatistics, logPaths: Boolean = false, pathFilter: (Path) -> Boolean = {true}) {
        forEachClassInJrt(jreHome) { path ->
            if (pathFilter(path)) {
                if (logPaths) {
                    println("Processing $path")
                }
                val fileName = path.toString()
                checkClassFile(fileName, JrtInputStreamProvider(fileName, jreHome), testStatistics)
            }
        }
    }

    data class TestStatistics(var count: Int = 0, var errors: Int = 0)

    private fun checkClassFile(fileName: String, inputStreamProvider: InputStreamProvider, testStatistics: TestStatistics) {
        val className = fileName.toClassNameWithModuleCheck()
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

    private fun String.toClassNameWithModuleCheck(): String {
        val matchResult = Regex("modules/(.*?)/(.*)").find(this)
        return if (matchResult != null) {
            matchResult.groupValues[1] + "/" + matchResult.groupValues[2].toClassName()
        } else {
            toClassName()
        }
    }

    private fun String.toClassName(): String {
        return removeSuffix(".class").replace("/", ".")
    }

    fun checkClassFile(resourcePath: String): Boolean {
        return checkClassFile(InputStreamProvider::class.java.getResource(resourcePath))
    }

    fun checkClassFile(url: URL): Boolean {
        return checkClassFile(null, UrlInputStreamProvider(url))
    }

    fun checkClassFile(className: String?, inputStreamProvider: InputStreamProvider): Boolean {
        val output = ByteArrayOutputStream()
        inputStreamProvider.createInputStream().use {
            it.copyTo(output)
        }
        val before = output.toByteArray()

        val classFile = ClassFileReader.readFromInputStream(inputStreamProvider.createInputStream())
        val after = ClassFileWriter.writeToByteArray(classFile)
        val reportedClassName = className ?: classFile.thisClassName.replace('/', '.')

        val success = compare(reportedClassName, before, after)
        if (!success) {
            isDebug = true
            println()
            println("*** before: ")
            ClassFileReader.readFromInputStream(ByteArrayInputStream(before))
            println()
            println("*** write: ")
            ClassFileWriter.writeToByteArray(classFile)
            println()
            println("*** after: ")
            ClassFileReader.readFromInputStream(ByteArrayInputStream(after))
            isDebug = false
        }
        return success
    }

    fun error(className: String) {
        System.err.println("ERROR when processing $className")
    }

    fun compare(className: String, before: ByteArray, after: ByteArray): Boolean {
        if (before.size != after.size) {
            System.err.println("ERROR in $className")
            System.err.println("Different length " + before.size + " != " + after.size)
        }
        for (i in 0 until minOf(before.size, after.size)) {
            if (before[i] != after[i]) {
                System.err.println("Different byte at index $i")
                System.err.println("" + before[i] +" != " + after[i])
                return false
            }
        }
        return true
    }
}
