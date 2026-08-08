/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.io.writeToByteArray
import org.gjt.jclasslib.testutil.readJdkClass
import org.gjt.jclasslib.testutil.resetSavingConfirmationPolicy
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.OptionAlertResult
import org.jclasslib.agent.ClassDescriptor
import org.jclasslib.agent.CommunicatorMBean
import org.jclasslib.agent.ReplacementResult
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import kotlin.test.*

class WriteClassFileTest {

    private val classFile = readJdkClass()

    @TempDir
    lateinit var tempDir: File

    @BeforeTest
    @AfterTest
    fun resetConfirmation() = resetSavingConfirmationPolicy()

    private class FakeCommunicator(private val result: ReplacementResult) : CommunicatorMBean {
        val replacedClassFiles = mutableListOf<Pair<String, ByteArray>>()

        override fun getClasses(): List<ClassDescriptor> = emptyList()

        override fun getClassFile(fileName: String): ByteArray? = null

        override fun replaceClassFile(fileName: String, bytes: ByteArray): ReplacementResult {
            replacedClassFiles.add(fileName to bytes)
            return result
        }
    }

    private fun overwrite() = OptionAlertResult(0, false)
    private fun chooseDirectory() = OptionAlertResult(1, false)
    private fun cancel() = OptionAlertResult(2, false)

    @Test
    fun testOverwriteWritesClassFileInPlace() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = overwrite()
        val target = File(tempDir, "Object.class")

        val result = writeClassFile(classFile, target.path, null, null) { error("no directory chooser expected") }

        assertTrue(result)
        assertContentEquals(classFile.writeToByteArray(), target.readBytes())
        assertEquals(1, alerts.optionDialogs.size)
        assertEquals(3, alerts.optionDialogs[0].options.size)
    }

    @Test
    fun testCancelWritesNothing() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = cancel()
        val target = File(tempDir, "Object.class")

        val result = writeClassFile(classFile, target.path, null, null) { error("no directory chooser expected") }

        assertFalse(result)
        assertFalse(target.exists())
    }

    @Test
    fun testChooseDirectorySavesCopyToDirectory() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = chooseDirectory()

        val result = writeClassFile(classFile, File(tempDir, "doesNotExist").path, null, null) { tempDir }

        assertTrue(result)
        assertContentEquals(classFile.writeToByteArray(), File(tempDir, "Object.class").readBytes())
    }

    @Test
    fun testCancelledDirectoryChooserWritesNothing() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = chooseDirectory()

        val result = writeClassFile(classFile, File(tempDir, "doesNotExist").path, null, null) { null }

        assertFalse(result)
        assertEquals(0, tempDir.list()!!.size)
    }

    @Test
    fun testJrtClassCanOnlyBeSavedToDirectory() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(0, false)

        val result = writeClassFile(classFile, "jrt:java.base/java/lang/Object.class", null, null) { tempDir }

        assertTrue(result)
        assertContentEquals(classFile.writeToByteArray(), File(tempDir, "Object.class").readBytes())
        // only the yes/no question is shown, never the "overwrite" confirmation
        assertEquals(1, alerts.optionDialogs.size)
        assertEquals(2, alerts.optionDialogs[0].options.size)
    }

    @Test
    fun testJrtClassSaveDeclinedWritesNothing() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(1, false)

        val result = writeClassFile(classFile, "jrt:java.base/java/lang/Object.class", null, null) { tempDir }

        assertFalse(result)
        assertEquals(0, tempDir.list()!!.size)
    }

    @Test
    fun testJarEntryIsUpdatedInArchive() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = overwrite()
        val jarFile = File(tempDir, "test.jar")
        JarOutputStream(jarFile.outputStream()).use { jar ->
            jar.putNextEntry(JarEntry("java/lang/Object.class"))
            jar.write(readJdkClass("java.lang.String").writeToByteArray())
            jar.closeEntry()
        }

        val result = writeClassFile(classFile, "${jarFile.path}!/java/lang/Object.class", null, null) { null }

        assertTrue(result)
        JarFile(jarFile).use { jar ->
            val entry = jar.getJarEntry("java/lang/Object.class")
            assertContentEquals(classFile.writeToByteArray(), jar.getInputStream(entry).readBytes())
        }
    }

    @Test
    fun testSuppressedConfirmationIsRememberedForSession() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(1, true)
        assertTrue(writeClassFile(classFile, File(tempDir, "a/Object.class").path, null, null) { tempDir })

        alerts.nextOptionDialogResult = cancel()
        assertTrue(writeClassFile(classFile, File(tempDir, "b/Object.class").path, null, null) { tempDir })

        assertEquals(1, alerts.optionDialogs.size)
    }

    @Test
    fun testSuppressedCancellationIsRememberedForSession() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(2, true)
        assertFalse(writeClassFile(classFile, File(tempDir, "Object.class").path, null, null) { tempDir })

        alerts.nextOptionDialogResult = overwrite()
        assertFalse(writeClassFile(classFile, File(tempDir, "Object.class").path, null, null) { tempDir })

        assertEquals(1, alerts.optionDialogs.size)
        assertFalse(File(tempDir, "Object.class").exists())
    }

    @Test
    fun testSaveToRunningVmRedefinesClass() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = overwrite()
        val communicator = FakeCommunicator(ReplacementResult.SUCCESS)

        val result = writeClassFile(classFile, "com.example.Foo", null, communicator) { null }

        assertTrue(result)
        assertEquals(1, communicator.replacedClassFiles.size)
        assertEquals("com.example.Foo", communicator.replacedClassFiles[0].first)
        assertContentEquals(classFile.writeToByteArray(), communicator.replacedClassFiles[0].second)
        assertTrue(alerts.messages.isEmpty())
    }

    @Test
    fun testVmRedefinitionRejectionShowsError() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = overwrite()
        val communicator = FakeCommunicator(ReplacementResult("schema change not supported"))

        val result = writeClassFile(classFile, "com.example.Foo", null, communicator) { null }

        assertFalse(result)
        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.ERROR, alerts.messages[0].alertType)
        assertTrue(alerts.messages[0].contentMessage!!.contains("schema change not supported"))
    }
}
