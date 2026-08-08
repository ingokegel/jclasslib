/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.testutil.TestBrowserServices
import org.gjt.jclasslib.testutil.selectNode
import org.gjt.jclasslib.testutil.selectedNode
import org.gjt.jclasslib.testutil.testBrowserComponent
import org.gjt.jclasslib.testutil.onEdt
import javax.swing.tree.TreePath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class NavigationHistoryTest {

    private fun services() = TestBrowserServices()

    @Test
    fun testSelectionAddsHistoryEntry() {
        val services = services()
        val component = testBrowserComponent(services)

        onEdt { component.selectNode { it.type == NodeType.GENERAL } }
        assertFalse(services.backwardAction.isEnabled)

        onEdt { component.selectNode { it.type == NodeType.CONSTANT_POOL } }
        assertTrue(services.backwardAction.isEnabled)
        assertFalse(services.forwardAction.isEnabled)
    }

    @Test
    fun testBackwardAndForwardMoveSelection() {
        val services = services()
        val component = testBrowserComponent(services)
        val general = onEdt { component.selectNode { it.type == NodeType.GENERAL } }
        val constantPool = onEdt { component.selectNode { it.type == NodeType.CONSTANT_POOL } }

        onEdt { component.history.historyBackward() }
        assertSame(general, onEdt { component.selectedNode() })
        assertTrue(services.forwardAction.isEnabled)

        onEdt { component.history.historyForward() }
        assertSame(constantPool, onEdt { component.selectedNode() })
    }

    @Test
    fun testReselectingCurrentNodeDoesNotAddHistoryEntry() {
        val services = services()
        val component = testBrowserComponent(services)
        onEdt { component.selectNode { it.type == NodeType.GENERAL } }
        val constantPool = onEdt { component.selectNode { it.type == NodeType.CONSTANT_POOL } }
        onEdt { component.selectNode { it == constantPool } }

        onEdt { component.history.historyBackward() }

        assertEquals(NodeType.GENERAL, onEdt { component.selectedNode() }.type)
    }

    @Test
    fun testDetailStateIsRestoredOnBackward() {
        val services = services()
        val component = testBrowserComponent(services)
        val generalPath = onEdt { TreePath(component.selectNode { it.type == NodeType.GENERAL }.path) }
        var resetCount = 0
        val resetter = object : BrowserHistory.Resetter {
            override fun reset() {
                resetCount++
            }
        }

        onEdt { component.history.addHistoryEntry(generalPath, resetter) }
        onEdt { component.selectNode { it.type == NodeType.CONSTANT_POOL } }
        onEdt { component.history.historyBackward() }

        assertEquals(1, resetCount)
        assertEquals(NodeType.GENERAL, onEdt { component.selectedNode() }.type)
    }

    @Test
    fun testBackwardAtStartIsDisabled() {
        val services = services()
        val component = testBrowserComponent(services)
        onEdt { component.selectNode { it.type == NodeType.GENERAL } }

        assertFalse(services.backwardAction.isEnabled)
        assertFalse(services.forwardAction.isEnabled)
    }
}
