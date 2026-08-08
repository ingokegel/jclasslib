/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.structures.MethodInfo
import org.gjt.jclasslib.testutil.BrowserAppFixture
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.modifyFirstUtf8
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.readJdkClass
import org.gjt.jclasslib.testutil.selectedNode
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.OptionAlertResult
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NavigationAppTest : SwingRobotTest() {

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

    private fun BrowserTab.selectedNode(): BrowserTreeNode =
        browserComponent.selectedNode()

    @Test
    fun testNewlyOpenedClassSelectsGeneralInformation() {
        val tab = fixture.openClass(readJdkClass(), File(tempDir, "Object.class"))

        assertEquals(NodeType.GENERAL, onEdt { tab.selectedNode() }.type)
    }

    @Test
    fun testReloadKeepsSelectionAndClearsHistory() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(0, false)
        val tab = fixture.openClass(readJdkClass(), File(tempDir, "Object.class"))
        onEdt { tab.modifyFirstUtf8("changed") }
        val methodNode = fixture.selectNode(tab) { (it.element as? MethodInfo)?.name == "equals" }
        fixture.selectNode(tab) { it.type == NodeType.GENERAL }
        fixture.selectNode(tab) { it == methodNode }
        assertTrue(fixture.frame.backwardAction.isEnabled)

        onEdt { tab.reload() }

        val selected = onEdt { tab.selectedNode() }
        assertEquals("equals", (selected.element as MethodInfo).name)

        // the pre-reload history is gone: one step back lands on the default selection, then it stops
        onEdt { tab.browserComponent.history.historyBackward() }
        assertEquals(NodeType.GENERAL, onEdt { tab.selectedNode() }.type)
        assertFalse(fixture.frame.backwardAction.isEnabled)
    }
}
