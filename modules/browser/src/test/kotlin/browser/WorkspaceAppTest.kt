/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.testutil.BrowserAppFixture
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.readJdkClass
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.testutil.workspaceFromXml
import org.gjt.jclasslib.testutil.workspaceToXml
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.OptionAlertResult
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class WorkspaceAppTest : SwingRobotTest() {

    @TempDir
    lateinit var tempDir: File

    private lateinit var fixture: BrowserAppFixture

    override fun onSetUp() {
        fixture = BrowserAppFixture()
        fixture.focusFrame(robot())
    }

    override fun onTearDown() {
        fixture.dispose()
    }

    @Test
    fun testWorkspaceRestoresConfigAndOpenTabs() {
        val classesDir = File(tempDir, "classes").apply { mkdirs() }
        fixture.openClass(readJdkClass(), File(tempDir, "Object.class"))
        fixture.openClass(readJdkClass("java.lang.String"), File(tempDir, "String.class"))
        onEdt {
            fixture.frame.config.addClasspathDirectory(classesDir.path)
            fixture.frame.config.jreHome = tempDir.path
        }
        val workspaceFile = File(tempDir, "test.jcw")
        workspaceFile.writeText(onEdt { workspaceToXml(fixture.frame.config, fixture.frame.frameContent) })

        onEdt {
            fixture.frame.config.clear()
            fixture.frame.frameContent.closeAllTabs(force = true)
            workspaceFromXml(workspaceFile.readText(), fixture.frame.config, fixture.frame.frameContent)
        }

        assertEquals(1, fixture.frame.config.classpath.size)
        assertEquals(tempDir.path, fixture.frame.config.jreHome)
        assertEquals(2, fixture.frame.frameContent.totalTabCount)
    }

    @Test
    fun testUnknownClassPromptsClasspathSetup() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(1, false)

        onEdt { fixture.frame.openClassFile("com.example.DoesNotExist", null) }

        assertEquals(1, alerts.optionDialogs.size)
        assertEquals(AlertType.WARNING, alerts.optionDialogs[0].alertType)
        assertEquals(2, alerts.optionDialogs[0].options.size)
        assertEquals(0, fixture.frame.frameContent.totalTabCount)
    }
}
