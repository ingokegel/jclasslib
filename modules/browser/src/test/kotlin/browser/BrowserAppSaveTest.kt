/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.io.writeToByteArray
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info
import org.gjt.jclasslib.testutil.BrowserAppFixture
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.modifyFirstUtf8
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.readJdkClass
import org.gjt.jclasslib.testutil.resetSavingConfirmationPolicy
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.OptionAlertResult
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BrowserAppSaveTest : SwingRobotTest() {

    @TempDir
    lateinit var tempDir: File

    private lateinit var fixture: BrowserAppFixture

    override fun onSetUp() {
        fixture = BrowserAppFixture()
    }

    override fun onTearDown() {
        fixture.dispose()
    }

    @BeforeTest
    @AfterTest
    fun resetConfirmation() = resetSavingConfirmationPolicy()

    @Test
    fun testSaveAllModifiedClassesWritesAllTabs() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(0, false)
        val objectClassFile = readJdkClass()
        val stringClassFile = readJdkClass("java.lang.String")
        val objectTab = fixture.openClass(objectClassFile, File(tempDir, "Object.class"))
        val stringTab = fixture.openClass(stringClassFile, File(tempDir, "String.class"))
        onEdt {
            objectTab.modifyFirstUtf8("changedA")
            stringTab.modifyFirstUtf8("changedB")
            fixture.frame.saveModifiedClassesAction()
        }

        assertContentEquals(objectTab.classFile.writeToByteArray(), File(tempDir, "Object.class").readBytes())
        assertContentEquals(stringTab.classFile.writeToByteArray(), File(tempDir, "String.class").readBytes())
        assertFalse(objectTab.browserComponent.isModified)
        assertFalse(stringTab.browserComponent.isModified)
        assertFalse(fixture.frame.saveModifiedClassesAction.isEnabled)
    }

    @Test
    fun testSaveAllCopiesToDirectoryKeepsModificationState() = withFakeAlertFacade { alerts ->
        val objectTab = fixture.openClass(readJdkClass(), File(tempDir, "Object.class"))
        fixture.openClass(readJdkClass("java.lang.String"), File(tempDir, "String.class"))
        onEdt { objectTab.modifyFirstUtf8("changed") }
        val targetDir = File(tempDir, "target")

        onEdt { fixture.frame.frameContent.saveClassesToDirectory(targetDir) }

        assertTrue(File(targetDir, "java/lang/Object.class").exists())
        assertTrue(File(targetDir, "java/lang/String.class").exists())
        assertTrue(objectTab.browserComponent.isModified)
        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.INFORMATION, alerts.messages[0].alertType)
    }

    @Test
    fun testReloadDiscardsUnsavedEditsAfterConfirmation() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(0, false)
        val classFile = readJdkClass()
        val originalString = classFile.constantPool.filterIsInstance<ConstantUtf8Info>().first().string
        val tab = fixture.openClass(classFile, File(tempDir, "Object.class"))
        onEdt { tab.modifyFirstUtf8("changed") }

        onEdt { tab.reload() }

        assertFalse(tab.browserComponent.isModified)
        assertEquals(originalString, tab.classFile.constantPool.filterIsInstance<ConstantUtf8Info>().first().string)
    }

    @Test
    fun testReloadCancelledKeepsUnsavedEdits() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(1, false)
        val tab = fixture.openClass(readJdkClass(), File(tempDir, "Object.class"))
        onEdt { tab.modifyFirstUtf8("changed") }

        onEdt { tab.reload() }

        assertTrue(tab.browserComponent.isModified)
        assertEquals("changed", tab.classFile.constantPool.filterIsInstance<ConstantUtf8Info>().first().string)
    }

    @Test
    fun testCloseAllTabsCancelledKeepsTabsOpen() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(1, false)
        val tab = fixture.openClass(readJdkClass(), File(tempDir, "Object.class"))
        onEdt { tab.modifyFirstUtf8("changed") }

        assertFalse(onEdt { fixture.frame.frameContent.closeAllTabs() })
        assertEquals(1, fixture.frame.frameContent.totalTabCount)
    }

    @Test
    fun testCloseAllTabsConfirmedClosesTabs() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(0, false)
        val tab = fixture.openClass(readJdkClass(), File(tempDir, "Object.class"))
        onEdt { tab.modifyFirstUtf8("changed") }

        assertTrue(onEdt { fixture.frame.frameContent.closeAllTabs() })
        assertEquals(0, fixture.frame.frameContent.totalTabCount)
    }
}
