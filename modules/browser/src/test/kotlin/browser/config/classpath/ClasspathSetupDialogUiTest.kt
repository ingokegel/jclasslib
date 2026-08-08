/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config.classpath

import org.assertj.swing.fixture.JButtonFixture
import org.assertj.swing.fixture.JListFixture
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.config.BrowserConfig
import org.gjt.jclasslib.testutil.BrowserAppFixture
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.findAllByType
import org.gjt.jclasslib.testutil.onEdt
import org.junit.jupiter.api.io.TempDir
import java.io.File
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JTextField
import javax.swing.SwingUtilities
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClasspathSetupDialogUiTest : SwingRobotTest() {

    @TempDir
    lateinit var tempDir: File

    private lateinit var fixture: BrowserAppFixture
    private lateinit var dialog: ClasspathSetupDialog

    override fun onSetUp() {
        fixture = BrowserAppFixture()
    }

    override fun onTearDown() {
        onEdt { dialog.dispose() }
        fixture.dispose()
    }

    private fun configWithThreeEntries(): BrowserConfig {
        val config = fixture.frame.config
        File(tempDir, "a").mkdirs()
        File(tempDir, "b").mkdirs()
        File(tempDir, "c").mkdirs()
        config.addClasspathDirectory(File(tempDir, "a").path)
        config.addClasspathDirectory(File(tempDir, "b").path)
        config.addClasspathDirectory(File(tempDir, "c").path)
        return config
    }

    private fun showDialog(): ClasspathSetupDialog {
        val dialog = onEdt { ClasspathSetupDialog(fixture.frame) }
        // the dialog is modal, showing it blocks the EDT in a nested event pump
        SwingUtilities.invokeLater { dialog.isVisible = true }
        return dialog
    }

    private fun entryNames(): List<String> = onEdt {
        fixture.frame.config.classpath.map { (it as ClasspathDirectoryEntry).file.name }
    }

    private fun listFixture(): JListFixture =
        JListFixture(robot(), robot().finder().findByType(dialog, JList::class.java, true))

    private fun actionButton(tooltipKey: String): JButton {
        val tooltip = getString(tooltipKey)
        return robot().findAllByType<JButton>(dialog).first { it.toolTipText == tooltip }
    }

    private fun clickActionButton(tooltipKey: String) {
        JButtonFixture(robot(), actionButton(tooltipKey)).click()
    }

    private fun clickTextButton(textKey: String) {
        JButtonFixture(robot(), robot().findAllByType<JButton>(dialog)
                .first { it.text == getString(textKey) }).click()
    }

    @Test
    fun testDialogExposesEntriesAndJreHome() {
        configWithThreeEntries()
        dialog = showDialog()

        assertEquals(3, listFixture().contents().size)
        val textField = robot().finder().findByType(dialog, JTextField::class.java, true)
        assertEquals(fixture.frame.config.jreHome, textField.text)
    }

    @Test
    fun testMoveUpReordersEntriesOnOk() {
        configWithThreeEntries()
        dialog = showDialog()

        listFixture().selectItem(1)
        clickActionButton("action.move.up.description")
        clickTextButton("action.ok")

        assertEquals(listOf("b", "a", "c"), entryNames())
    }

    @Test
    fun testMoveDownReordersEntriesOnOk() {
        configWithThreeEntries()
        dialog = showDialog()

        listFixture().selectItem(0)
        clickActionButton("action.move.down.description")
        clickTextButton("action.ok")

        assertEquals(listOf("b", "a", "c"), entryNames())
    }

    @Test
    fun testBoundaryMoveButtonsAreDisabled() {
        configWithThreeEntries()
        dialog = showDialog()

        listFixture().selectItem(0)
        assertFalse(actionButton("action.move.up.description").isEnabled)
        assertTrue(actionButton("action.move.down.description").isEnabled)

        listFixture().selectItem(2)
        assertTrue(actionButton("action.move.up.description").isEnabled)
        assertFalse(actionButton("action.move.down.description").isEnabled)
    }

    @Test
    fun testRemoveEntryIsAppliedOnOk() {
        configWithThreeEntries()
        dialog = showDialog()

        listFixture().selectItem(1)
        clickActionButton("action.classpath.remove.entry.description")
        clickTextButton("action.ok")

        assertEquals(listOf("a", "c"), entryNames())
    }

    @Test
    fun testCancelLeavesConfigUntouched() {
        configWithThreeEntries()
        dialog = showDialog()

        listFixture().selectItem(1)
        clickActionButton("action.classpath.remove.entry.description")
        clickTextButton("action.cancel")

        assertEquals(listOf("a", "b", "c"), entryNames())
    }
}
