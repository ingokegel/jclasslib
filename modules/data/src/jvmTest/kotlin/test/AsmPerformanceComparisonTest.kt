/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.test

import org.gjt.jclasslib.io.*
import org.gjt.jclasslib.structures.SYSTEM_PROPERTY_SKIP_ATTRIBUTES
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.jar.JarFile
import kotlin.test.Test

class AsmPerformanceComparisonTest {

    @Test
    fun testJresReadOnly() {
        for (javaHome in getJavaHomes()) {
            println("Testing with $javaHome")
            scanJre(javaHome, Mode.READ_ONLY)
        }
    }

    @Test
    fun testJresReadOnlySkipAttributes() {
        for (javaHome in getJavaHomes()) {
            println("Testing with $javaHome")
            scanJre(javaHome, Mode.READ_ONLY_SKIP_ATTRIBUTES)
        }
    }

    @Test
    fun testJresReadWrite() {
        for (javaHome in getJavaHomes()) {
            println("Testing with $javaHome")
            scanJre(javaHome, Mode.READ_WRITE)
        }
    }

    @Test
    fun testJresReadWriteBytecode() {
        for (javaHome in getJavaHomes()) {
            println("Testing with $javaHome")
            scanJre(javaHome, Mode.READ_WRITE_BYTECODE)
        }
    }

    private enum class Mode { READ_ONLY, READ_ONLY_SKIP_ATTRIBUTES, READ_WRITE, READ_WRITE_BYTECODE }

    private fun scanJre(javaHome: File, mode: Mode) {
        val rtJar = javaHome.resolve("lib/rt.jar")

        var count = 0
        var jclasslibNanos = 0L
        var asmNanos = 0L

        if (mode == Mode.READ_ONLY_SKIP_ATTRIBUTES) {
            System.setProperty(SYSTEM_PROPERTY_SKIP_ATTRIBUTES, "true")
        }

        val processClass: (String, InputStreamProvider) -> Unit = { _, provider ->
            val bytes = provider.createInputStream().use { input ->
                ByteArrayOutputStream().also { input.copyTo(it) }.toByteArray()
            }

            val jclasslibStart = System.nanoTime()
            val classFile = ClassFileReader.readFromInputStream(bytes.inputStream())
            if (mode == Mode.READ_WRITE_BYTECODE) {
                classFile.methods.forEach { methodInfo ->
                    methodInfo.attributes.filterIsInstance<CodeAttribute>().firstOrNull()?.let { codeAttribute ->
                        codeAttribute.code = writeByteCode(readByteCode(codeAttribute.code))
                    }
                }
            }
            if (mode != Mode.READ_ONLY && mode != Mode.READ_ONLY_SKIP_ATTRIBUTES) {
                ClassFileWriter.writeToByteArray(classFile)
            }
            jclasslibNanos += System.nanoTime() - jclasslibStart

            val asmStart = System.nanoTime()
            val reader = ClassReader(bytes)
            if (mode == Mode.READ_ONLY || mode == Mode.READ_ONLY_SKIP_ATTRIBUTES) {
                val asmFlags = if (mode == Mode.READ_ONLY_SKIP_ATTRIBUTES) {
                    ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES
                } else {
                    0
                }
                reader.accept(object : ClassVisitor(Opcodes.ASM9) {}, asmFlags)
            } else {
                val writer = ClassWriter(0)
                reader.accept(writer, 0)
                writer.toByteArray()
            }
            asmNanos += System.nanoTime() - asmStart

            count++
        }

        if (rtJar.exists()) {
            val jar = JarFile(rtJar)
            for (entry in jar.entries().iterator()) {
                val fileName = entry.name.removeSuffix("/")
                if (fileName.endsWith(".class")) {
                    processClass(fileName, JarInputStreamProvider(jar, entry))
                }
            }
        } else {
            forEachClassInJrt(javaHome) { _, path ->
                val fileName = path.toString()
                processClass(fileName, JrtInputStreamProvider(fileName, javaHome))
            }
        }

        if (mode == Mode.READ_ONLY_SKIP_ATTRIBUTES) {
            System.clearProperty(SYSTEM_PROPERTY_SKIP_ATTRIBUTES)
        }

        val jclasslibMs = jclasslibNanos / 1_000_000
        val asmMs = asmNanos / 1_000_000
        println("$count classes [${mode.name}]: jclasslib ${jclasslibMs}ms, ASM ${asmMs}ms, ratio %.2f".format(jclasslibMs.toDouble() / asmMs))
    }

    private fun getJavaHomes(): List<File> {
        val majorVersions = System.getProperty("majorVersions", "")
            .split(",")
            .mapNotNull { it.toIntOrNull() }
        return majorVersions.map {
            requireNotNull(System.getProperty("javaHome.$it")) {
                "Java $it home not specified"
            }
        }.ifEmpty { listOf(System.getProperty("java.home")) }.map { getJavaHome(it) }
    }

    private fun getJavaHome(javaHomePath: String): File {
        val javaHome = File(javaHomePath)
        val jreHome = javaHome.resolve("jre")
        return if (jreHome.exists()) jreHome else javaHome
    }
}
