/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.testutil.BrowserAppFixture
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.clickMenuItemWithText
import org.gjt.jclasslib.testutil.confirm
import org.gjt.jclasslib.testutil.descendants
import org.gjt.jclasslib.testutil.findActivePopupMenuFixture
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.readJdkClass
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.AlertType
import org.junit.jupiter.api.io.TempDir
import java.awt.Point
import java.io.File
import javax.swing.JTextPane
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ByteCodeEditorAppTest : SwingRobotTest() {

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

    private fun classFileWithInitCode(vararg code: Int): ClassFile {
        val classFile = readJdkClass()
        val initMethod = classFile.methods.first { it.name == "<init>" }
        val codeAttribute = initMethod.attributes.filterIsInstance<CodeAttribute>().first()
        codeAttribute.code = code.map { it.toByte() }.toByteArray()
        return classFile
    }

    private fun clickInstructionLink(tab: BrowserTab, mnemonic: String) {
        fixture.selectNode(tab) { it.element is CodeAttribute }
        val (textPane, point) = onEdt {
            val pane = fixture.frame.frameContent.selectedTab!!.browserComponent.detailPane.currentDetailPane
            val textPane = pane.descendants().filterIsInstance<JTextPane>().first { it.isShowing }
            val position = textPane.text.indexOf(mnemonic)
            check(position >= 0) { "instruction '$mnemonic' not found in bytecode document" }
            val rect = textPane.modelToView2D(position).bounds
            textPane.scrollRectToVisible(rect)
            textPane to Point(rect.x + 2, rect.y + rect.height / 2)
        }
        robot().click(textPane, point)
    }

    @Test
    fun testReplaceOpcodeViaInstructionLink() = withFakeAlertFacade { alerts ->
        // iconst_1, iconst_1, iadd, return
        val tab = fixture.openClass(classFileWithInitCode(4, 4, 96, 177), File(tempDir, "Object.class"))

        clickInstructionLink(tab, "iadd")
        findActivePopupMenuFixture(robot()).clickMenuItemWithText(getString("action.replace.opcode"))
        expectOptionPane { dialog ->
            dialog.comboBox().selectItem("isub")
            dialog.confirm()
        }

        val codeAttribute = tab.classFile.methods.first { it.name == "<init>" }
                .attributes.filterIsInstance<CodeAttribute>().first()
        assertContentEquals(byteArrayOf(4, 4, 100, 177.toByte()), codeAttribute.code)
        assertTrue(tab.browserComponent.isModified)
        assertTrue(alerts.messages.isEmpty())
    }

    @Test
    fun testReplaceOpcodeWithoutCompatibleReplacementsShowsWarning() = withFakeAlertFacade { alerts ->
        // goto_w +5, return
        val tab = fixture.openClass(classFileWithInitCode(200, 0, 0, 0, 5, 177), File(tempDir, "Object.class"))

        clickInstructionLink(tab, "goto_w")
        findActivePopupMenuFixture(robot()).clickMenuItemWithText(getString("action.replace.opcode"))

        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.WARNING, alerts.messages[0].alertType)
        assertEquals(getString("no.compatible.opcode"), alerts.messages[0].mainMessage)
        assertFalse(tab.browserComponent.isModified)
    }
}
