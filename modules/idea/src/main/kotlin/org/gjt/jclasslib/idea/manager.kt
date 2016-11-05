/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.ide.impl.ContentManagerWatcher
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager

class BytecodeContentManager(val project: Project) : ProjectComponent {

    override fun initComponent() {
    }

    override fun disposeComponent() {
    }

    override fun getComponentName(): String {
        return "BytecodeContentManager"
    }

    override fun projectOpened() {
        ToolWindowManager.getInstance(project).registerToolWindow(TOOL_WINDOW_ID, true, ToolWindowAnchor.RIGHT, project).apply {
            setIcon(ShowBytecodeAction.ICON)
            ContentManagerWatcher(this, getContentManager())
        }
    }

    override fun projectClosed() {
    }

    companion object {
        val TOOL_WINDOW_ID: String = "jclasslib"
    }
}
