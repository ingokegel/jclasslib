/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.constants

import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.browser.detail.ActionBuilderImpl
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.ConstantPoolUtil
import org.gjt.jclasslib.structures.constants.ConstantMethodrefInfo
import org.gjt.jclasslib.structures.constants.ConstantStringInfo
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.TestBrowserServices
import org.gjt.jclasslib.testutil.TestDetailPane
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.readJdkClass
import org.gjt.jclasslib.testutil.confirm
import org.gjt.jclasslib.testutil.cancel
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.AlertType
import javax.swing.JMenuItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DelegatesEditorUiTest : SwingRobotTest() {

    enum class TestEnum { FIRST, SECOND, THIRD }

    class EnumHolder {
        var value = TestEnum.FIRST
    }

    private class EnumEditor : DelegatesEditor<EnumHolder>() {
        override fun DelegateBuilder<EnumHolder>.buildDelegateSpecs() {
            addEnumSpec("Value", TestEnum::class.java, EnumHolder::value)
        }

        fun initData(holder: EnumHolder) {
            data = holder
        }
    }

    private class UnsupportedDelegateEditor : DelegatesEditor<Constant>() {
        override fun DelegateBuilder<Constant>.buildDelegateSpecs() = Unit

        fun editUnsupported(constant: Constant, detailPane: DetailPane<*>) = editDelegate(constant, detailPane, "test")
    }

    private val classFile = ClassFile()
    private lateinit var services: TestBrowserServices
    private lateinit var detailPane: TestDetailPane

    override fun onSetUp() {
        services = TestBrowserServices()
        detailPane = onEdt { TestDetailPane(services) }
    }

    private fun buildMenuItem(editor: EnumEditor): JMenuItem = onEdt {
        val builder = ActionBuilderImpl()
        with(editor) {
            builder.buildActions(detailPane)
        }
        assertEquals(1, builder.popupMenu.componentCount)
        builder.popupMenu.getComponent(0) as JMenuItem
    }

    @Test
    fun testEnumChoiceIsAppliedAndMarksModified() = withFakeAlertFacade { alerts ->
        val holder = EnumHolder()
        val menuItem = buildMenuItem(EnumEditor().apply { initData(holder) })
        driveInputDialog({ dialog ->
            dialog.comboBox().selectItem("SECOND")
            dialog.confirm()
        }) {
            menuItem.doClick()
        }
        assertEquals(TestEnum.SECOND, holder.value)
        assertEquals(1, services.modifiedCount)
        assertTrue(alerts.messages.isEmpty())
    }

    @Test
    fun testUnchangedEnumChoiceDoesNotMarkModified() = withFakeAlertFacade { alerts ->
        val holder = EnumHolder()
        val menuItem = buildMenuItem(EnumEditor().apply { initData(holder) })
        driveInputDialog({ it.confirm() }) {
            menuItem.doClick()
        }
        assertEquals(TestEnum.FIRST, holder.value)
        assertEquals(0, services.modifiedCount)
        assertTrue(alerts.messages.isEmpty())
    }

    @Test
    fun testCancelledEnumChoiceLeavesValueUnchanged() = withFakeAlertFacade { alerts ->
        val holder = EnumHolder()
        val menuItem = buildMenuItem(EnumEditor().apply { initData(holder) })
        driveInputDialog({ dialog ->
            dialog.comboBox().selectItem("THIRD")
            dialog.cancel()
        }) {
            menuItem.doClick()
        }
        assertEquals(TestEnum.FIRST, holder.value)
        assertEquals(0, services.modifiedCount)
        assertTrue(alerts.messages.isEmpty())
    }

    @Test
    fun testLinkedEditModifiesTargetUtf8Constant() = withFakeAlertFacade { alerts ->
        val realClassFile = readJdkClass()
        val utf8Index = ConstantPoolUtil.addConstantUTF8Info(realClassFile, "old")
        val constant = ConstantStringInfo(realClassFile).apply { stringIndex = utf8Index }
        driveInputDialog("new") {
            ConstantStringEditor().edit(constant, detailPane, null)
        }
        assertEquals("new", constant.utf8Constant.string)
        assertEquals(1, services.modifiedCount)
        assertTrue(alerts.messages.isEmpty())
    }

    @Test
    fun testUnsupportedLinkedEditShowsWarning() = withFakeAlertFacade { alerts ->
        UnsupportedDelegateEditor().editUnsupported(ConstantMethodrefInfo(classFile), detailPane)
        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.WARNING, alerts.messages[0].alertType)
        assertEquals(0, services.modifiedCount)
    }
}
