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
import org.gjt.jclasslib.testutil.workspaceFromXml
import org.gjt.jclasslib.testutil.workspaceToXml
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkspaceConfigTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun testWorkspaceRoundTripPreservesEntriesAndOrder() {
        val config = BrowserConfig()
        val dir = File(tempDir, "classes").apply { mkdirs() }
        val jar = File(tempDir, "lib.jar").apply { writeBytes(byteArrayOf()) }
        config.jreHome = tempDir.path
        config.addClasspathDirectory(dir.path)
        config.addClasspathArchive(jar.path)

        val restored = BrowserConfig()
        workspaceFromXml(workspaceToXml(config), restored)

        assertEquals(2, restored.classpath.size)
        assertEquals(ClasspathDirectoryEntry(dir.path), restored.classpath[0])
        assertEquals(ClasspathArchiveEntry(jar.path), restored.classpath[1])
        assertEquals(tempDir.path, restored.jreHome)
    }

    @Test
    fun testWorkspaceLoadIgnoresMissingJreHome() {
        val config = BrowserConfig()
        config.jreHome = File(tempDir, "doesNotExist").path

        val restored = BrowserConfig()
        workspaceFromXml(workspaceToXml(config), restored)

        assertEquals(System.getProperty("java.home"), restored.jreHome)
    }

    @Test
    fun testWorkspaceLoadReplacesCurrentConfig() {
        val config = BrowserConfig()
        val dir = File(tempDir, "classes").apply { mkdirs() }
        config.addClasspathDirectory(dir.path)

        val restored = BrowserConfig()
        restored.addClasspathArchive(File(tempDir, "old.jar").path)
        workspaceFromXml(workspaceToXml(config), restored)

        assertEquals(listOf<ClasspathEntry>(ClasspathDirectoryEntry(dir.path)), restored.classpath)
    }

    @Test
    fun testWorkspaceWithoutClasspathElementClearsConfig() {
        val restored = BrowserConfig()
        restored.addClasspathDirectory(tempDir.path)

        workspaceFromXml("<workspace/>", restored)

        assertTrue(restored.classpath.isEmpty())
    }
}
