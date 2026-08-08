/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config

import org.gjt.jclasslib.browser.config.classpath.ClasspathArchiveEntry
import org.gjt.jclasslib.browser.config.classpath.ClasspathDirectoryEntry
import org.gjt.jclasslib.browser.config.classpath.ClasspathEntry
import org.gjt.jclasslib.io.getJrtInputStream
import org.gjt.jclasslib.io.writeToByteArray
import org.gjt.jclasslib.testutil.readJdkClass
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ClasspathConfigTest {

    @TempDir
    lateinit var tempDir: File

    private fun createClassDirectory(name: String, vararg classNames: String, moduleInfo: Boolean = false): File {
        val directory = File(tempDir, name)
        for (className in classNames) {
            val classFile = File(directory, className.replace('.', '/') + ".class")
            classFile.parentFile.mkdirs()
            classFile.writeBytes(readJdkClass(className).writeToByteArray())
        }
        if (moduleInfo) {
            getJrtInputStream("java.base/module-info.class", File(System.getProperty("java.home"))).use { input ->
                File(directory, "module-info.class").writeBytes(input.readBytes())
            }
        }
        return directory
    }

    private fun createJar(name: String, vararg entryNames: String): File {
        val jarFile = File(tempDir, name)
        JarOutputStream(jarFile.outputStream()).use { jar ->
            for (entryName in entryNames) {
                jar.putNextEntry(JarEntry(entryName))
                jar.write(readJdkClass("java.lang.Object").writeToByteArray())
                jar.closeEntry()
            }
        }
        return jarFile
    }

    @Test
    fun testAddEntryAppendsInOrder() {
        val config = BrowserConfig()
        val dirA = createClassDirectory("a", "java.lang.Object")
        val dirB = createClassDirectory("b", "java.lang.String")

        config.addClasspathDirectory(dirA.path)
        config.addClasspathDirectory(dirB.path)

        assertEquals(listOf(dirA.canonicalFile, dirB.canonicalFile),
                config.classpath.map { (it as ClasspathDirectoryEntry).file })
    }

    @Test
    fun testDuplicateEntryIsNotAdded() {
        val config = BrowserConfig()
        val dir = createClassDirectory("a", "java.lang.Object")

        config.addClasspathDirectory(dir.path)
        config.addClasspathDirectory(dir.path)

        assertEquals(1, config.classpath.size)
    }

    @Test
    fun testDuplicateEntryWithDifferentSpellingIsNotAdded() {
        val config = BrowserConfig()
        val dir = createClassDirectory("a", "java.lang.Object")
        File(dir, "sub").mkdirs()

        config.addClasspathDirectory(dir.path)
        config.addClasspathDirectory(File(dir, "sub/..").path)

        assertEquals(1, config.classpath.size)
    }

    @Test
    fun testDirectoryAndArchiveWithSamePathAreDistinctEntries() {
        val config = BrowserConfig()
        val dir = createClassDirectory("a", "java.lang.Object")

        config.addClasspathDirectory(dir.path)
        config.addClasspathArchive(dir.path)

        assertEquals(2, config.classpath.size)
    }

    @Test
    fun testRemoveEntry() {
        val config = BrowserConfig()
        val dir = createClassDirectory("a", "java.lang.Object")
        config.addClasspathDirectory(dir.path)

        config.removeClasspathEntry(config.classpath.first())

        assertTrue(config.classpath.isEmpty())
    }

    @Test
    fun testClearResetsToDefaults() {
        val config = BrowserConfig()
        config.jreHome = tempDir.path
        config.addClasspathDirectory(createClassDirectory("a", "java.lang.Object").path)

        config.clear()

        assertTrue(config.classpath.isEmpty())
        assertEquals(System.getProperty("java.home"), config.jreHome)
    }

    @Test
    fun testFindClassReturnsFirstMatchingEntry() {
        val config = BrowserConfig()
        val dirA = createClassDirectory("a", "java.lang.Object")
        val dirB = createClassDirectory("b", "java.lang.Object")
        config.addClasspathDirectory(dirA.path)
        config.addClasspathDirectory(dirB.path)

        val result = config.findClass("java.lang.Object", false)

        assertNotNull(result)
        assertEquals(File(dirA, "java/lang/Object.class").path, result.fileName)
    }

    @Test
    fun testFindClassSkipsNonMatchingEntries() {
        val config = BrowserConfig()
        config.addClasspathDirectory(createClassDirectory("a", "java.lang.String").path)
        val dirB = createClassDirectory("b", "java.lang.Object")
        config.addClasspathDirectory(dirB.path)

        val result = config.findClass("java.lang.Object", false)

        assertNotNull(result)
        assertEquals(File(dirB, "java/lang/Object.class").path, result.fileName)
    }

    @Test
    fun testFindClassInArchive() {
        val config = BrowserConfig()
        val jarFile = createJar("test.jar", "java/lang/Object.class")
        config.addClasspathArchive(jarFile.path)

        val result = config.findClass("java.lang.Object", false)

        assertNotNull(result)
        assertEquals("${jarFile.path}!java/lang/Object.class", result.fileName)
        assertEquals(ClasspathEntry.UNNAMED_MODULE, result.moduleName)
    }

    @Test
    fun testFindClassInArchiveWithObfuscatedDirectoryEntry() {
        val config = BrowserConfig()
        val jarFile = createJar("test.jar", "java/lang/Object.class/")
        config.addClasspathArchive(jarFile.path)

        val result = config.findClass("java.lang.Object", false)

        assertNotNull(result)
        assertEquals("${jarFile.path}!java/lang/Object.class", result.fileName)
    }

    @Test
    fun testModulePathSelectionRequiresMatchingModule() {
        val config = BrowserConfig()
        val dir = createClassDirectory("a", "java.lang.Object", moduleInfo = true)
        config.addClasspathDirectory(dir.path)

        assertNotNull(config.findClass("java.base/java.lang.Object", true))
        assertNull(config.findClass("other.module/java.lang.Object", true))
    }

    @Test
    fun testClasspathSelectionIgnoresModule() {
        val config = BrowserConfig()
        val dir = createClassDirectory("a", "java.lang.Object", moduleInfo = true)
        config.addClasspathDirectory(dir.path)

        val result = config.findClass("java.lang.Object", false)

        assertNotNull(result)
        assertEquals("java.base", result.moduleName)
    }

    @Test
    fun testFindClassFallsBackToJre() {
        val config = BrowserConfig()
        config.addClasspathDirectory(createClassDirectory("a", "java.lang.String").path)

        val result = config.findClass("java.lang.Object", false)

        assertNotNull(result)
        assertTrue(result.fileName.startsWith("jrt:"), "expected a jrt reference, got ${result.fileName}")
        assertTrue(result.fileName.contains("java/lang/Object.class"))
    }

    @Test
    fun testJreIsNotConsultedWhenEntryMatches() {
        val config = BrowserConfig()
        val dir = createClassDirectory("a", "java.lang.Object")
        config.addClasspathDirectory(dir.path)

        val result = config.findClass("java.lang.Object", false)

        assertNotNull(result)
        assertEquals(File(dir, "java/lang/Object.class").path, result.fileName)
    }

    @Test
    fun testFindClassWithoutJreReturnsNull() {
        val config = BrowserConfig()
        config.jreHome = File(tempDir, "noJre").apply { mkdirs() }.path
        config.addClasspathDirectory(createClassDirectory("a", "java.lang.String").path)

        assertNull(config.findClass("java.lang.Object", false))
    }

    @Test
    fun testEntryEqualityUsesCanonicalPath() {
        val dir = createClassDirectory("a", "java.lang.Object")
        File(dir, "sub").mkdirs()

        assertEquals(ClasspathDirectoryEntry(dir.path), ClasspathDirectoryEntry(File(dir, "sub/..").path))
        assertEquals(ClasspathArchiveEntry("x.jar"), ClasspathArchiveEntry("x.jar"))
        assertTrue(ClasspathDirectoryEntry(dir.path) != ClasspathArchiveEntry(dir.path))
    }
}
