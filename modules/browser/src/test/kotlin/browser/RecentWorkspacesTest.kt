/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.testutil.onEdt
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.prefs.Preferences
import javax.swing.JMenuItem
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecentWorkspacesTest {

    @TempDir
    lateinit var tempDir: File

    private val preferencesNode = Preferences.userRoot().node("jclasslib-test-${System.nanoTime()}")
    private val frame: BrowserFrame = onEdt { BrowserFrame() }
    private val recentMenu = RecentMenu(frame)

    @AfterTest
    fun tearDown() {
        onEdt { frame.dispose() }
        preferencesNode.removeNode()
    }

    private fun workspaceFile(name: String) = File(tempDir, name)

    private fun canonical(name: String) = workspaceFile(name).canonicalPath

    private fun savedFiles(): List<String> {
        onEdt { recentMenu.save(preferencesNode) }
        val node = preferencesNode.node("recentWorkspaces")
        return node.keys().sortedBy { it.toInt() }.map { node.get(it, null) }
    }

    @Test
    fun testMostRecentlyUsedComesFirst() {
        onEdt {
            recentMenu.addRecentWorkspace(workspaceFile("a.jcw"))
            recentMenu.addRecentWorkspace(workspaceFile("b.jcw"))
        }

        assertEquals(
            listOf(canonical("b.jcw"), canonical("a.jcw")),
            savedFiles()
        )
    }

    @Test
    fun testReuseMovesEntryToFrontWithoutDuplicating() {
        onEdt {
            recentMenu.addRecentWorkspace(workspaceFile("a.jcw"))
            recentMenu.addRecentWorkspace(workspaceFile("b.jcw"))
            recentMenu.addRecentWorkspace(workspaceFile("a.jcw"))
        }

        assertEquals(
            listOf(canonical("a.jcw"), canonical("b.jcw")),
            savedFiles()
        )
    }

    @Test
    fun testListIsCappedAtTen() {
        onEdt {
            for (i in 1..12) {
                recentMenu.addRecentWorkspace(workspaceFile("workspace-$i.jcw"))
            }
        }

        val saved = savedFiles()
        assertEquals(10, saved.size)
        assertEquals(canonical("workspace-12.jcw"), saved.first())
        assertEquals(canonical("workspace-3.jcw"), saved.last())
    }

    @Test
    fun testReadRestoresSavedOrder() {
        onEdt {
            recentMenu.addRecentWorkspace(workspaceFile("a.jcw"))
            recentMenu.addRecentWorkspace(workspaceFile("b.jcw"))
            recentMenu.save(preferencesNode)
        }

        val restored = onEdt { RecentMenu(frame).apply { read(preferencesNode) } }
        onEdt { restored.save(preferencesNode) }

        assertEquals(
            listOf(canonical("b.jcw"), canonical("a.jcw")),
            savedFiles()
        )
    }

    @Test
    fun testClearListEmptiesRecents() {
        onEdt {
            recentMenu.addRecentWorkspace(workspaceFile("a.jcw"))
            recentMenu.menuSelectionChanged(true)
        }
        val clearItem = onEdt {
            recentMenu.menuComponents.filterIsInstance<JMenuItem>().last()
        }

        onEdt { clearItem.doClick() }

        assertTrue(savedFiles().isEmpty())
    }
}
