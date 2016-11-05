/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.ide.actions.CloseTabToolbarAction
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.content.Content

class BytecodeToolWindowPanel : SimpleToolWindowPanel(true, true) {

    lateinit var content: Content

    init {
        val actionGroup = DefaultActionGroup().apply {
            add(CloseAction())
        }
        ActionManager.getInstance().createActionToolbar("bytecodeToolBar", actionGroup, true).apply {
            setTargetComponent(this@BytecodeToolWindowPanel)
            setToolbar(component)
        }
    }

    private inner class CloseAction : CloseTabToolbarAction() {
        override fun actionPerformed(e: AnActionEvent) {
            content.getManager().removeContent(content, true)
        }
    }

}
