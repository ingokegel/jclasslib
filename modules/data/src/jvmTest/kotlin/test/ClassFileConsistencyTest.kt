/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.test

import org.gjt.jclasslib.io.*
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.structures.isDebug
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.test.Test

class ClassFileConsistencyTest {
    @Test
    fun testJres() {
        withAllJres { javaHome ->
            scanJre(javaHome)
        }
    }

    @Test
    fun testJreModules() {
        withAllJres { javaHome ->
            scanJreModules(javaHome)
        }
    }

    @Test
    fun testSingleClasses() {
        checkJavaClassFile("Main.class")
        checkResourceClassFile("/moduleMainClass/module-info.class")
    }

    private fun scanJre(javaHome: File) {
        val testStatistics = TestStatistics()
        val rtJar = javaHome.resolve("lib/rt.jar")
        if (rtJar.exists()) {
            scanJar(rtJar, testStatistics)
        } else {
            scanJrt(javaHome, testStatistics)
        }
        println("${testStatistics.count} classes checked, ${testStatistics.errors} errors")
    }

    private fun scanJreModules(javaHome: File, logPaths: Boolean = false) {
        val rtJar = javaHome.resolve("lib/rt.jar")
        if (rtJar.exists()) {
            println("Not a modular JRE")
            return
        }
        val testStatistics = TestStatistics()
        scanJrt(javaHome, testStatistics, logPaths) { path ->
            path.endsWith("module-info.class")
        }
        println("${testStatistics.count} modules checked, ${testStatistics.errors} errors")
    }

    private fun scanJar(file: File, testStatistics: TestStatistics) {
        val jar = JarFile(file)
        for (entry in jar.entries().iterator()) {
            val fileName = entry.name
            if (fileName.endsWith(".class")) {
                checkClassFile(fileName, JarInputStreamProvider(jar, entry), testStatistics)
            }
        }
    }

    private fun scanJrt(jreHome: File, testStatistics: TestStatistics, logPaths: Boolean = false, pathFilter: (Path) -> Boolean = { true }) {
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

    private data class TestStatistics(var count: Int = 0, var errors: Int = 0)

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

    private fun withAllJres(block: (File) -> Unit) {
        for (javaHome in getJavaHomes()) {
            println("Testing with $javaHome")
            block(javaHome)
        }
    }

    private fun getJavaHomes(): List<File> {
        val majorVersions = System.getProperty("majorVersions", "")
                .split(",")
                .mapNotNull { it.toIntOrNull() }
        val javaHomes = majorVersions.map {
            requireNotNull(System.getProperty("javaHome.$it")) {
                "Java $it home not specified"
            }
        }.ifEmpty { listOf(System.getProperty("java.home")) }.map { getJavaHome(it) }
        return javaHomes
    }

    private fun getJavaHome(javaHomePath: String): File {
        val javaHome = File(javaHomePath)
        val jreHome = javaHome.resolve("jre")
        return if (jreHome.exists()) {
            jreHome
        } else {
            javaHome
        }
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

    private fun checkJavaClassFile(resourcePath: String): Boolean {
        return checkClassFile(resourcePath, createJavaClassFileInputStreamProvider(resourcePath))
    }

    private fun checkResourceClassFile(resourcePath: String): Boolean {
        return checkClassFile(resourcePath, createResourceClassFileInputStreamProvider(resourcePath))
    }

    private fun checkClassFile(className: String, inputStreamProvider: InputStreamProvider): Boolean {
        val output = ByteArrayOutputStream()
        inputStreamProvider.createInputStream().use {
            it.copyTo(output)
        }
        val before = output.toByteArray()

        val classFile = ClassFileReader.readFromInputStream(inputStreamProvider.createInputStream())
        classFile.methods.forEach { methodInfo ->
            methodInfo.attributes.filterIsInstance<CodeAttribute>().firstOrNull()?.let { codeAttribute ->
                val codeBefore = codeAttribute.code
                val instructions = readByteCode(codeBefore)
                val codeAfter = writeByteCode(instructions)
                if (!codeBefore.contentEquals(codeAfter)) {
                    println("ERROR in $className: bytecode difference for ${methodInfo.name}")
                }
            }
        }

        val after = ClassFileWriter.writeToByteArray(classFile)

        val success = compare(className, before, after)
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

    private fun error(className: String) {
        println("ERROR when processing $className")
    }

    private fun compare(className: String, before: ByteArray, after: ByteArray): Boolean {
        if (before.size != after.size) {
            println("ERROR in $className")
            println("Different length " + before.size + " != " + after.size)
        }
        for (i in 0 until minOf(before.size, after.size)) {
            if (before[i] != after[i]) {
                println("ERROR in $className")
                println("Different byte at index $i")
                println("" + before[i] + " != " + after[i])
                return false
            }
        }
        return true
    }
}
