/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.test

import org.gjt.jclasslib.io.*
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.objectweb.asm.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.jar.JarFile
import kotlin.test.Test

class AsmPerformanceComparisonTest {

    @Test
    fun testJresReadOnly() {
        scanJresWithWarmup(Mode.READ_ONLY)
    }

    @Test
    fun testJresReadOnlySkipAttributes() {
        scanJresWithWarmup(Mode.READ_ONLY_SKIP_ATTRIBUTES)
    }

    @Test
    fun testJresReadWrite() {
        scanJresWithWarmup(Mode.READ_WRITE)
    }

    @Test
    fun testJresReadWriteBytecode() {
        scanJresWithWarmup(Mode.READ_WRITE_BYTECODE)
    }

    private enum class Mode { READ_ONLY, READ_ONLY_SKIP_ATTRIBUTES, READ_WRITE, READ_WRITE_BYTECODE }

    private fun scanJre(javaHome: File, mode: Mode, warmup: Boolean = false) {
        val rtJar = javaHome.resolve("lib/rt.jar")

        var count = 0
        var jclasslibNanos = 0L
        var asmNanos = 0L

        val readMode = if (mode == Mode.READ_ONLY_SKIP_ATTRIBUTES) ClassFileReadMode.SKIP_ATTRIBUTES else ClassFileReadMode.FULL

        val processClass: (String, InputStreamProvider) -> Unit = { _, provider ->
            val bytes = provider.createInputStream().use { input ->
                ByteArrayOutputStream().also { input.copyTo(it) }.toByteArray()
            }

            val jclasslibStart = System.nanoTime()
            val classFile = ClassFileReader.readFromInputStream(bytes.inputStream(), readMode = readMode)
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
                reader.accept(FullReadClassVisitor, asmFlags)
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

        if (!warmup) {
            val jclasslibMs = jclasslibNanos / 1_000_000
            val asmMs = asmNanos / 1_000_000
            println("$count classes [${mode.name}]: jclasslib ${jclasslibMs}ms, ASM ${asmMs}ms, ratio %.2f".format(jclasslibMs.toDouble() / asmMs))
        }
    }

    private fun scanJresWithWarmup(mode: Mode) {
        val javaHomes = getJavaHomes()
        println("Warmup with ${javaHomes.first()}")
        scanJre(javaHomes.first(), mode, warmup = true)
        for (javaHome in javaHomes) {
            println("Testing with $javaHome")
            scanJre(javaHome, mode)
        }
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

    /**
     * A ClassVisitor that returns non-null sub-visitors to force ASM to fully
     * parse all structures, making the comparison with jclasslib more meaningful.
     */
    private object FullReadClassVisitor : ClassVisitor(Opcodes.ASM9) {
        private val annotationVisitor = object : AnnotationVisitor(Opcodes.ASM9) {
            override fun visitAnnotation(name: String?, descriptor: String?) = this
            override fun visitArray(name: String?) = this
        }

        private val methodVisitor = object : MethodVisitor(Opcodes.ASM9) {
            override fun visitAnnotationDefault() = annotationVisitor
            override fun visitAnnotation(descriptor: String?, visible: Boolean) = annotationVisitor
            override fun visitTypeAnnotation(typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean) = annotationVisitor
            override fun visitParameterAnnotation(parameter: Int, descriptor: String?, visible: Boolean) = annotationVisitor
            override fun visitInsnAnnotation(typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean) = annotationVisitor
            override fun visitTryCatchAnnotation(typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean) = annotationVisitor
            override fun visitLocalVariableAnnotation(typeRef: Int, typePath: TypePath?, start: Array<out Label>?, end: Array<out Label>?, index: IntArray?, descriptor: String?, visible: Boolean) = annotationVisitor
        }

        private val fieldVisitor = object : FieldVisitor(Opcodes.ASM9) {
            override fun visitAnnotation(descriptor: String?, visible: Boolean) = annotationVisitor
            override fun visitTypeAnnotation(typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean) = annotationVisitor
        }

        private val recordComponentVisitor = object : RecordComponentVisitor(Opcodes.ASM9) {
            override fun visitAnnotation(descriptor: String?, visible: Boolean) = annotationVisitor
            override fun visitTypeAnnotation(typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean) = annotationVisitor
        }

        override fun visitAnnotation(descriptor: String?, visible: Boolean) = annotationVisitor
        override fun visitTypeAnnotation(typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean) = annotationVisitor
        override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?) = methodVisitor
        override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?) = fieldVisitor
        override fun visitRecordComponent(name: String?, descriptor: String?, signature: String?) = recordComponentVisitor
        override fun visitModule(name: String?, access: Int, version: String?) = object : ModuleVisitor(Opcodes.ASM9) {}
    }
}
