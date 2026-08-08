/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.usages

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.io.writeToByteArray
import org.gjt.jclasslib.structures.ConstantPoolUtil
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info
import org.gjt.jclasslib.testutil.BrowserAppFixture
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.clickButtonWithText
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.readJdkClass
import org.gjt.jclasslib.testutil.selectedNode
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.AlertType
import org.junit.jupiter.api.io.TempDir
import java.io.File
import javax.swing.SwingUtilities
import kotlin.test.Test
import kotlin.test.assertEquals

class FindStringAppTest : SwingRobotTest() {

    @TempDir
    lateinit var tempDir: File

    private lateinit var fixture: BrowserAppFixture

    override fun onSetUp() {
        fixture = BrowserAppFixture()
    }

    override fun onTearDown() {
        fixture.dispose()
    }

    private fun setupClasspathWithMarker(marker: String) {
        val classFile = readJdkClass()
        ConstantPoolUtil.addConstantUTF8Info(classFile, marker)
        val classesDir = File(tempDir, "classes")
        val classFileOnDisk = File(classesDir, "java/lang/Object.class")
        classFileOnDisk.parentFile.mkdirs()
        classFileOnDisk.writeBytes(classFile.writeToByteArray())
        onEdt { fixture.frame.config.addClasspathDirectory(classesDir.path) }
    }

    private fun runStringSearch(spec: String) {
        SwingUtilities.invokeLater { fixture.frame.searchStringAction() }
        expectDialog(getString("find.string.title")) { dialog ->
            dialog.textBox().enterText(spec)
            dialog.clickButtonWithText(getString("action.ok"))
        }
    }

    @Test
    fun testFindStringOpensClassAtMatchingConstant() = withFakeAlertFacade {
        val marker = "needle_marker_string"
        setupClasspathWithMarker(marker)

        runStringSearch(marker)
        expectDialog(getString("found.classes.with.usages.title")) { results ->
            results.list().selectItem(0)
            results.clickButtonWithText(getString("action.ok"))
        }

        assertEquals(1, fixture.frame.frameContent.totalTabCount)
        val tab = fixture.frame.frameContent.selectedTab!!
        val selected = onEdt { tab.browserComponent.selectedNode() }
        assertEquals(marker, (selected.element as ConstantUtf8Info).string)
    }

    @Test
    fun testFindStringWithoutMatchShowsInfo() = withFakeAlertFacade { alerts ->
        setupClasspathWithMarker("needle_marker_string")

        runStringSearch("no_such_string_anywhere")

        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.INFORMATION, alerts.messages[0].alertType)
        assertEquals(0, fixture.frame.frameContent.totalTabCount)
    }
}
