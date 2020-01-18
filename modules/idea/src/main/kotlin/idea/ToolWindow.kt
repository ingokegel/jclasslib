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
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.fileTypes.StdFileTypes
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
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
import org.gjt.jclasslib.browser.WEBSITE_URL
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.structures.ClassFile
import java.awt.event.ActionEvent
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import javax.swing.AbstractAction
import javax.swing.Action

const val TOOL_WINDOW_ID: String = "jclasslib"

fun showClassFile(locatedClassFile: LocatedClassFile, browserPath: BrowserPath?, project: Project) {
    val toolWindow = getToolWindow(project)
    val existingEntry = toolWindow.contentManager.contents
        .associateBy { it.component as BytecodeToolWindowPanel }
        .entries.firstOrNull { it.key.locatedClassFile == locatedClassFile }
    if (existingEntry != null) {
        val (panel, content) = existingEntry
        activateToolWindow(toolWindow, content, panel, browserPath)
    } else {
        val classFile = readClassFile(locatedClassFile, project)
        if (classFile != null) {
            val panel = BytecodeToolWindowPanel(classFile, locatedClassFile, project)
            panel.browserComponent.browserPath = browserPath
            val content = toolWindow.contentManager.run {
                val content = factory.createContent(panel, locatedClassFile.virtualFile.name, false)
                panel.content = content
                addContent(content)
                content
            }
            activateToolWindow(toolWindow, content, panel, browserPath)
        }
    }
}

private fun readClassFile(locatedClassFile: LocatedClassFile, project: Project): ClassFile? {
    locatedClassFile.virtualFile.refresh(false, false)
    return try {
        val classFileBytes = loadClassFileBytes(locatedClassFile, project)
        ClassFileReader.readFromInputStream(ByteArrayInputStream(classFileBytes))
    } catch(e: Exception) {
        Messages.showWarningDialog(project, "Error reading class file: ${e.message}", "jclasslib bytecode viewer")
        null
    }
}

fun loadClassFileBytes(locatedClassFile: LocatedClassFile, project: Project): ByteArray =
    if (FileTypeRegistry.getInstance().isFileOfType(locatedClassFile.virtualFile, StdFileTypes.CLASS)) {
        loadCompiledClassFileBytes(locatedClassFile, project)
    } else {
        loadSourceClassFileBytes(locatedClassFile, project)
    }

private fun loadCompiledClassFileBytes(locatedClassFile: LocatedClassFile, project: Project): ByteArray {
    val index = ProjectFileIndex.SERVICE.getInstance(project)
    val file = locatedClassFile.virtualFile
    val classFileName = StringUtil.getShortName(locatedClassFile.jvmClassName) + ".class"
    return if (index.isInLibraryClasses(file)) {
        val classFile = file.parent.findChild(classFileName)
        classFile?.contentsToByteArray(false) ?: throw IOException("Class file not found")
    } else {
        val classFile = File(file.parent.path, classFileName)
        if (classFile.isFile) {
            FileUtil.loadFileBytes(classFile)
        } else {
            throw IOException("Class file not found")
        }
    }
}

private fun loadSourceClassFileBytes(locatedClassFile: LocatedClassFile, project: Project): ByteArray {
    val index = ProjectFileIndex.SERVICE.getInstance(project)
    val file = locatedClassFile.virtualFile
    val module = index.getModuleForFile(file) ?: throw IOException("Module not found")
    val extension = CompilerModuleExtension.getInstance(module) ?: throw IOException("Extension not found")
    val inTests = index.isInTestSourceContent(file)
    val classPathRoot = (if (inTests) extension.compilerOutputPathForTests else extension.compilerOutputPath)
        ?: throw IOException("Class root not found")
    val relativePath = locatedClassFile.jvmClassName.replace('.', '/') + ".class"
    val classFile = File(classPathRoot.path, relativePath)
    return if (classFile.exists()) {
        FileUtil.loadFileBytes(classFile)
    } else {
        throw IOException("Class file not found")
    }
}

private fun activateToolWindow(toolWindow: ToolWindow, content: Content, panel: BytecodeToolWindowPanel, browserPath: BrowserPath?) {
    panel.browserComponent.browserPath = browserPath
    toolWindow.contentManager.setSelectedContent(content, true)
    toolWindow.activate(null)
}

private fun getToolWindow(project: Project): ToolWindow {
    val toolWindowManager = ToolWindowManager.getInstance(project)
    return toolWindowManager.getToolWindow(TOOL_WINDOW_ID) ?:
            toolWindowManager.registerToolWindow(TOOL_WINDOW_ID, true, ToolWindowAnchor.RIGHT, project).apply {
                icon = ShowBytecodeAction.ICON
                ContentManagerWatcher(this, contentManager)
            }
}

class BytecodeToolWindowPanel(override var classFile: ClassFile, val locatedClassFile: LocatedClassFile, val project: Project) : SimpleToolWindowPanel(true, true), BrowserServices {

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
            val newClassFile = readClassFile(locatedClassFile, project)
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
            showURL(WEBSITE_URL)
        }
    }

    override fun activate() {
    }

    override val browserComponent: BrowserComponent = BrowserComponent(this)
    override val backwardAction: Action = ActionDelegate(backwardActionDelegate)
    override val forwardAction: Action = ActionDelegate(forwardActionDelegate)

    override fun openClassFile(className: String, browserPath: BrowserPath?) {
        val jvmClassName = className.replace('.', '/')
        val virtualClassFile = getRoot().findFileByRelativePath("$jvmClassName.class")
        if (virtualClassFile != null) {
            showClassFile(LocatedClassFile(jvmClassName, virtualClassFile), browserPath, project)
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
        var root = locatedClassFile.virtualFile
        repeat(classFile.thisClassName.count { it == '/' } + 1) {
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
