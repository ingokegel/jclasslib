/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.assertj.swing.fixture.JButtonFixture
import org.gjt.jclasslib.browser.detail.KeyValueDetailPane
import org.gjt.jclasslib.structures.ConstantPoolUtil
import org.gjt.jclasslib.structures.constants.ConstantIntegerInfo
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info
import org.gjt.jclasslib.testutil.BrowserAppFixture
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.readJdkClass
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.junit.jupiter.api.io.TempDir
import java.io.File
import javax.swing.JButton
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BrowserAppEditTest : SwingRobotTest() {

    @TempDir
    lateinit var tempDir: File

    private lateinit var fixture: BrowserAppFixture

    override fun onSetUp() {
        fixture = BrowserAppFixture()
    }

    override fun onTearDown() {
        fixture.dispose()
    }

    private fun findEditButton(): JButton =
        robot().finder().findByName(fixture.frame, KeyValueDetailPane.EDIT_BUTTON_NAME, JButton::class.java, true)

    @Test
    fun testEditButtonIsShownForWritableClass() {
        val tab = fixture.openClass(readJdkClass(), File(tempDir, "Object.class"))
        fixture.selectNode(tab) { it.element is ConstantUtf8Info }

        assertNotNull(findEditButton())
    }

    @Test
    fun testScalarEditViaTreeAndDialogMarksClassModified() = withFakeAlertFacade { alerts ->
        val classFile = readJdkClass()
        ConstantPoolUtil.addConstantPoolEntry(classFile, ConstantIntegerInfo(classFile).apply { int = 5 })
        val tab = fixture.openClass(classFile, File(tempDir, "Object.class"))
        fixture.selectNode(tab) { (it.element as? ConstantIntegerInfo)?.int == 5 }

        JButtonFixture(robot(), findEditButton()).click()
        expectOptionPane("42")

        assertEquals(42, tab.classFile.constantPool.filterIsInstance<ConstantIntegerInfo>().last().int)
        assertTrue(tab.browserComponent.isModified)
        assertTrue(tab.getTabTitle().startsWith("* "))
        assertTrue(fixture.frame.saveModifiedClassesAction.isEnabled)
        assertTrue(alerts.messages.isEmpty())
    }
}
