/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.icons.AllIcons
import com.intellij.ide.BrowserUtil
import com.intellij.ide.DataManager
import com.intellij.ide.actions.CloseTabToolbarAction
import com.intellij.ide.impl.ContentManagerWatcher
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiAnonymousClass
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.content.Content
import com.intellij.util.PlatformIcons
import org.gjt.jclasslib.browser.BrowserComponent
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.webSiteUrl
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.structures.ClassFile
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Action

val toolWindowId: String = "jclasslib"

fun showClassFile(virtualFile: VirtualFile, browserPath: BrowserPath?, project: Project) {
    val toolWindow = getToolWindow(project)
    val existingEntry = toolWindow.contentManager.contents.associateBy { it.component as BytecodeToolWindowPanel }.entries.firstOrNull { it.key.virtualFile == virtualFile }
    if (existingEntry != null) {
        val (panel, content) = existingEntry
        activateToolWindow(toolWindow, content, panel, browserPath)
    } else {
        val classFile = readClassFile(virtualFile, project)
        if (classFile != null) {
            val panel = BytecodeToolWindowPanel(classFile, virtualFile, project)
            panel.browserComponent.browserPath = browserPath
            val content = toolWindow.contentManager.run {
                val content = factory.createContent(panel, virtualFile.name, false)
                panel.content = content
                addContent(content)
                content
            }
            activateToolWindow(toolWindow, content, panel, browserPath)
        }
    }
}

private fun readClassFile(virtualFile: VirtualFile, project: Project): ClassFile? {
    virtualFile.refresh(false, false)
    return try {
        ClassFileReader.readFromInputStream(virtualFile.inputStream)
    } catch(e: Exception) {
        Messages.showWarningDialog(project, "Error reading class file: ${e.message}", "jclasslib bytecode viewer")
        null
    }
}

private fun activateToolWindow(toolWindow: ToolWindow, content: Content, panel: BytecodeToolWindowPanel, browserPath: BrowserPath?) {
    panel.browserComponent.browserPath = browserPath
    toolWindow.contentManager.setSelectedContent(content, true)
    toolWindow.activate(null)
}

private fun getToolWindow(project: Project): ToolWindow {
    val toolWindowManager = ToolWindowManager.getInstance(project)
    return toolWindowManager.getToolWindow(toolWindowId) ?:
            toolWindowManager.registerToolWindow(toolWindowId, true, ToolWindowAnchor.RIGHT, project).apply {
                icon = ShowBytecodeAction.ICON
                ContentManagerWatcher(this, contentManager)
            }
}

class BytecodeToolWindowPanel(override var classFile: ClassFile, val virtualFile: VirtualFile, val project: Project) : SimpleToolWindowPanel(true, true), BrowserServices {

    lateinit var content: Content

    private val closeAction: AnAction = object : CloseTabToolbarAction() {
        override fun actionPerformed(e: AnActionEvent) {
            content.manager.removeContent(content, true)
        }
    }

    private val backwardActionDelegate: AnAction = object : DumbAwareAction() {
        init {
            templatePresentation.apply {
                icon = AllIcons.Actions.Back
                text = "Backward"
            }
        }

        override fun actionPerformed(e: AnActionEvent) {
            browserComponent.history.historyBackward()
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = backwardAction.isEnabled
        }
    }

    private val forwardActionDelegate: AnAction = object : DumbAwareAction() {
        init {
            templatePresentation.apply {
                icon = AllIcons.Actions.Forward
                text = "Forward"
            }
        }

        override fun actionPerformed(e: AnActionEvent) {
            browserComponent.history.historyForward()
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = forwardAction.isEnabled
        }
    }

    private val reloadAction: AnAction = object : DumbAwareAction() {
        init {
            templatePresentation.apply {
                icon = PlatformIcons.SYNCHRONIZE_ICON
                text = "Reload"
            }
        }

        override fun actionPerformed(e: AnActionEvent) {
            val newClassFile = readClassFile(virtualFile, project)
            if (newClassFile != null) {
                classFile = newClassFile
                browserComponent.rebuild()
            }
        }
    }

    private val webAction: AnAction = object : DumbAwareAction() {
        init {
            templatePresentation.apply {
                icon = PlatformIcons.WEB_ICON
                text = "Show web site"
            }
        }

        override fun actionPerformed(e: AnActionEvent) {
            showURL(webSiteUrl)
        }
    }

    override fun activate() {
    }

    override val browserComponent: BrowserComponent = BrowserComponent(this)
    override val backwardAction: Action = ActionDelegate(backwardActionDelegate)
    override val forwardAction: Action = ActionDelegate(forwardActionDelegate)

    override fun openClassFile(className: String, browserPath: BrowserPath?) {
        val virtualClassFile = getRoot().findFileByRelativePath(className.replace('.', '/') + ".class")
        if (virtualClassFile != null) {
            showClassFile(virtualClassFile, browserPath, project)
        } else {
            val psiClass = findClass(className)
            if (psiClass != null) {
                openClassFile(psiClass, browserPath, project)
            } else {
                Messages.showWarningDialog(project, "Class $className could not be found", "jclasslib bytecode viewer")
            }
        }
    }

    private fun getRoot() : VirtualFile {
        var root = virtualFile
        (0..classFile.thisClassName.count { it == '/' }).forEach {
            root = root.parent
        }
        return root
    }

    override fun canOpenClassFiles(): Boolean = true

    override fun showURL(urlSpec: String) {
        BrowserUtil.browse(urlSpec)
    }

    init {
        val actionGroup = DefaultActionGroup().apply {
            add(closeAction)
            add(backwardActionDelegate)
            add(forwardActionDelegate)
            add(reloadAction)
            addSeparator()
            add(webAction)
        }
        val actionToolbar = ActionManager.getInstance().createActionToolbar("bytecodeToolBar", actionGroup, true).apply {
            setTargetComponent(this@BytecodeToolWindowPanel)
        }
        setToolbar(actionToolbar.component)
        setContent(browserComponent)
    }

    private inner class ActionDelegate(private val anAction: AnAction) : AbstractAction() {
        override fun actionPerformed(e: ActionEvent?) {
            anAction.actionPerformed(AnActionEvent.createFromAnAction(anAction, null, ActionPlaces.TOOLBAR, DataManager.getInstance().getDataContext(browserComponent)))
        }
    }

    private fun findClass(className: String): PsiClass? {
        return if (className.contains('$')) {
            findClass(null, className.split("\\$".toRegex()).toTypedArray(), 0)
        } else {
            findClass(null, className)
        }
    }

    private fun findClass(parent: PsiClass?, names: Array<String>, index: Int): PsiClass? {
        for (i in index until names.size) {
            val psiClass = findClass(parent, names.slice(index..i).joinToString("$"))
            if (psiClass != null) {
                if (i < names.size - 1) {
                    val maxPsiClass = findClass(psiClass, names, i + 1)
                    if (maxPsiClass != null) {
                        return maxPsiClass
                    }
                } else {
                    return psiClass
                }
            }
        }
        return null
    }

    private fun findClass(parent: PsiClass?, name: String): PsiClass? = when {
        parent == null -> JavaPsiFacade.getInstance(project).findClass(name, GlobalSearchScope.allScope(project))
        Character.isJavaIdentifierStart(name[0]) -> parent.findInnerClassByName(name, false)
        name.matches("\\d+".toRegex()) -> findAnonymousClass(parent, Integer.parseInt(name) - 1)
        else -> null
    }

    private fun findAnonymousClass(psiClass: PsiClass, index: Int): PsiClass? {
        val classes = psiClass.getAnonymousClasses()
        return if (index >= 0 && index < classes.size) classes[index] as PsiClass else null
    }

    private fun PsiClass.getAnonymousClasses(): Array<out PsiElement> = PsiTreeUtil.collectElements(this) { e ->
        e is PsiAnonymousClass && PsiTreeUtil.getParentOfType(e, PsiClass::class.java) == this
    }

    companion object {
        init {
            initUiFacades()
        }
    }
}
