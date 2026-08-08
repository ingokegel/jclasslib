/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.detail.attributes.CodeAttributeDetailPane
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import org.gjt.jclasslib.testutil.*
import java.awt.Point
import java.awt.event.MouseEvent
import javax.swing.JPopupMenu
import javax.swing.JTextPane
import javax.swing.tree.TreePath
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ByteCodeEditGatingTest : SwingRobotTest() {

    private fun popupMenuTexts(writable: Boolean): List<String> {
        val services = TestBrowserServices()
        services.canSaveClassFilesResult = writable
        val window = showInWindow(onEdt { services.browserComponent })
        try {
            onEdt {
                val component = services.browserComponent
                val node = component.findNode { it.element is CodeAttribute }
                component.treePane.tree.selectionPath = TreePath(node.path)
            }
            val textPane = onEdt {
                val codePane = services.browserComponent.detailPane.attributeDetailPane
                        .getDetailPane(CodeAttribute::class.java) as CodeAttributeDetailPane
                codePane.byteCodeDetailPane.descendants().filterIsInstance<JTextPane>().first()
            }
            val point = onEdt {
                val position = textPane.text.indexOf("return")
                textPane.modelToView2D(position).bounds.let { Point(it.x + 2, it.y + it.height / 2) }
            }
            onEdt {
                textPane.dispatchEvent(MouseEvent(textPane, MouseEvent.MOUSE_CLICKED,
                        System.currentTimeMillis(), 0, point.x, point.y, 1, false))
            }
            robot().waitForIdle()
            val popupMenu = robot().finder().findByType(JPopupMenu::class.java, true)
            return onEdt {
                popupMenu.components.filterIsInstance<javax.swing.JMenuItem>().map { it.text }
            }.also {
                onEdt { popupMenu.isVisible = false }
            }
        } finally {
            onEdt { window.dispose() }
        }
    }

    @Test
    fun testEditActionsOfferedWhenWritable() {
        val texts = popupMenuTexts(writable = true)
        assertTrue(texts.contains(getString("action.replace.opcode")))
    }

    @Test
    fun testEditActionsHiddenWhenNotWritable() {
        val texts = popupMenuTexts(writable = false)
        assertFalse(texts.contains(getString("action.replace.opcode")))
        assertTrue(texts.contains(getString("action.show.jvm.spec")))
    }
}
