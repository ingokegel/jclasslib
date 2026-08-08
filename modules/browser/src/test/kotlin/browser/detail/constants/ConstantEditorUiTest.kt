/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.constants

import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.constants.ConstantIntegerInfo
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.TestBrowserServices
import org.gjt.jclasslib.testutil.TestDetailPane
import org.gjt.jclasslib.testutil.confirm
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.AlertType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConstantEditorUiTest : SwingRobotTest() {

    private val classFile = ClassFile()
    private lateinit var services: TestBrowserServices
    private lateinit var detailPane: TestDetailPane

    override fun onSetUp() {
        services = TestBrowserServices()
        detailPane = onEdt { TestDetailPane(services) }
    }

    @Test
    fun testValidEditIsAppliedAndMarksModified() = withFakeAlertFacade { alerts ->
        val constant = ConstantIntegerInfo(classFile).apply { int = 5 }
        driveInputDialog("42") {
            ConstantIntegerEditor().edit(constant, detailPane)
        }
        assertEquals(42, constant.int)
        assertEquals(1, services.modifiedCount)
        assertTrue(services.browserComponent.isModified)
        assertTrue(alerts.messages.isEmpty())
    }

    @Test
    fun testUnchangedEditDoesNotMarkModified() = withFakeAlertFacade { alerts ->
        val constant = ConstantIntegerInfo(classFile).apply { int = 5 }
        driveInputDialog({ it.confirm() }) {
            ConstantIntegerEditor().edit(constant, detailPane)
        }
        assertEquals(5, constant.int)
        assertEquals(0, services.modifiedCount)
        assertTrue(alerts.messages.isEmpty())
    }

    @Test
    fun testInvalidEditShowsErrorAndLeavesConstantUnchanged() = withFakeAlertFacade { alerts ->
        val constant = ConstantIntegerInfo(classFile).apply { int = 5 }
        driveInputDialog("abc") {
            ConstantIntegerEditor().edit(constant, detailPane)
        }
        assertEquals(5, constant.int)
        assertEquals(0, services.modifiedCount)
        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.ERROR, alerts.messages[0].alertType)
    }

    @Test
    fun testCancelledEditLeavesConstantUnchanged() = withFakeAlertFacade { alerts ->
        val constant = ConstantIntegerInfo(classFile).apply { int = 5 }
        driveInputDialog(null) {
            ConstantIntegerEditor().edit(constant, detailPane)
        }
        assertEquals(5, constant.int)
        assertEquals(0, services.modifiedCount)
        assertTrue(alerts.messages.isEmpty())
    }

    @Test
    fun testUtf8EditAcceptsAnyStringViaDialog() = withFakeAlertFacade { alerts ->
        val constant = ConstantUtf8Info(classFile).apply { string = "old" }
        driveInputDialog("new value") {
            ConstantUtf8Editor().edit(constant, detailPane)
        }
        assertEquals("new value", constant.string)
        assertEquals(1, services.modifiedCount)
        assertTrue(alerts.messages.isEmpty())
    }
}
