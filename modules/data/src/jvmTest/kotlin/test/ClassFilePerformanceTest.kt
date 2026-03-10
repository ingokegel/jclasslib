/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.test

import org.gjt.jclasslib.io.ClassFileReader.readFromInputStream
import org.gjt.jclasslib.io.forEachClassInJrt
import org.gjt.jclasslib.structures.ClassFile
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.test.Test

class ClassFilePerformanceTest {
    @Test
    fun test() {
        benchmark(ClassFile::class.java)
    }


    @Test
    fun testReadAllClasses() {
        val jreHome = File(System.getProperty("java.home"))
        warmupReadAllClasses(jreHome)
        measureTime {
            measureReadAllClasses(jreHome)
        }
    }

    private fun benchmark(clazz: Class<*>) {
        val bytes = File(clazz.getResource("${clazz.simpleName}.class")!!.toURI()).readBytes()
        warmupSingleRead(bytes)
        measureTime { measureSingleRead(bytes) }
    }

    private fun warmupSingleRead(bytes: ByteArray) {
        repeat(1000) {
            singleRead(bytes)
        }
    }

    private fun measureSingleRead(bytes: ByteArray) {
        singleRead(bytes)
    }

    private fun singleRead(bytes: ByteArray) {
        readFromInputStream(ByteArrayInputStream(bytes))
    }

    private fun measureTime(block: () -> Unit) {
        val start = System.nanoTime()
        block()
        println("time: ${(System.nanoTime() - start) / 1_000_000.0} ms")
    }


    private fun warmupReadAllClasses(jreHome: File) {
        repeat(3) {
            readAllClasses(jreHome)
        }
    }

    private fun measureReadAllClasses(jreHome: File) {
        readAllClasses(jreHome)
    }


    private fun readAllClasses(jreHome: File) {
        forEachClassInJrt(jreHome) { _, path ->
            readFromInputStream(JrtInputStreamProvider(path.toString(), jreHome).createInputStream())
        }
    }

}
