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
import com.intellij.ide.highlighter.JavaClassFileType
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.compiler.CompilerPaths
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiAnonymousClass
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentManagerEvent
import com.intellij.ui.content.ContentManagerListener
import com.intellij.util.PlatformIcons
import org.gjt.jclasslib.browser.BrowserComponent
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.WEBSITE_URL
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.browser.writeClassFile
import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.util.GUIHelper
import org.gjt.jclasslib.util.GUIHelper.getParentWindow
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import javax.swing.*

const val TOOL_WINDOW_ID: String = "jclasslib"

fun showClassFile(locatedClassFile: LocatedClassFile, browserPath: BrowserPath?, project: Project) {
    val toolWindow = getToolWindow(project)
    val contentManager = toolWindow.contentManager
    val existingEntry = contentManager.contents
            .filter { it.component is BytecodeToolWindowPanel }
            .associateBy { it.component as BytecodeToolWindowPanel }
            .entries.firstOrNull { it.key.locatedClassFile == locatedClassFile }
    if (existingEntry != null) {
        val (panel, content) = existingEntry
        activateToolWindow(toolWindow, content, panel, browserPath)
    } else {
        object : Task.Backgroundable(project, "Loading class file", false) {
            var classFile: ClassFile? = null
            override fun run(indicator: ProgressIndicator) {
                classFile = readClassFile(locatedClassFile, project)
            }

            override fun onSuccess() {
                classFile?.let {classFile ->
                    val panel = BytecodeToolWindowPanel(classFile, locatedClassFile, project)
                    panel.browserComponent.browserPath = browserPath
                    val content = contentManager.run {
                        val content = factory.createContent(panel, locatedClassFile.virtualFile.name, false)
                        panel.content = content
                        addContent(content)
                        content
                    }
                    contentManager.contents.filter { it.component !is BytecodeToolWindowPanel }.forEach {
                        contentManager.removeContent(it, true)
                    }
                    activateToolWindow(toolWindow, content, panel, browserPath)
                }
            }
        }
        .queue()
    }
}

private fun readClassFile(locatedClassFile: LocatedClassFile, project: Project): ClassFile? {
    locatedClassFile.virtualFile.refresh(false, false)
    return try {
        val classFileBytes = ReadAction.compute<ByteArray, Throwable> {
            loadClassFileBytes(locatedClassFile, project)
        }
        ClassFileReader.readFromInputStream(ByteArrayInputStream(classFileBytes))
    } catch (e: Exception) {
        showWarningDialog("Error reading class file: ${e.message}", project)
        null
    }
}

private fun showWarningDialog(text: String, project: Project) {
    ApplicationManager.getApplication().invokeLater {
        Messages.showWarningDialog(project, text, GUIHelper.MESSAGE_TITLE)
    }
}

fun loadClassFileBytes(locatedClassFile: LocatedClassFile, project: Project): ByteArray =
    if (FileTypeRegistry.getInstance().isFileOfType(locatedClassFile.virtualFile, JavaClassFileType.INSTANCE)) {
        loadCompiledClassFileBytes(locatedClassFile, project)
    } else {
        loadSourceClassFileBytes(locatedClassFile, project)
    }

private fun loadCompiledClassFileBytes(locatedClassFile: LocatedClassFile, project: Project): ByteArray {
    val index = ProjectFileIndex.getInstance(project)
    val file = locatedClassFile.virtualFile
    val classFileName = StringUtil.getShortName(locatedClassFile.jvmClassName) + ".class"
    return if (index.isInLibraryClasses(file)) {
        val classFile = file.parent.findChild(classFileName)
        classFile?.contentsToByteArray(false).also {
            locatedClassFile.writableUrl = classFile?.url
        } ?: throw IOException("Class file not found")
    } else {
        val classFile = File(file.parent.path, classFileName)
        if (classFile.isFile) {
            locatedClassFile.writableUrl = classFile.toURI().path
            FileUtil.loadFileBytes(classFile)
        } else {
            throw IOException("Class file not found")
        }
    }
}

private fun loadSourceClassFileBytes(locatedClassFile: LocatedClassFile, project: Project): ByteArray {
    val index = ProjectRootManager.getInstance(project).fileIndex
    val module = index.getModuleForFile(locatedClassFile.virtualFile) ?: throw IOException("Module not found")

    val relativePath = locatedClassFile.jvmClassName.replace('.', '/') + ".class"
    for (path in CompilerPaths.getOutputPaths(arrayOf(module)).toList()) {
        val classFile = File(path, relativePath)
        if (classFile.exists()) {
            locatedClassFile.writableUrl = classFile.toURI().path
            return FileUtil.loadFileBytes(classFile)
        }
    }
    throw IOException("Class file not found")
}

private fun activateToolWindow(toolWindow: ToolWindow, content: Content, panel: BytecodeToolWindowPanel, browserPath: BrowserPath?) {
    panel.browserComponent.browserPath = browserPath
    toolWindow.contentManager.setSelectedContent(content, true)
    toolWindow.activate(null)
}

private fun getToolWindow(project: Project): ToolWindow {
    return requireNotNull(ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID))
}

private fun addInfoPanel(toolWindow: ToolWindow) {
    val infoPanel = JPanel().apply {
        layout = BorderLayout()
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(Box.createVerticalGlue())
            add(JPanel().apply {
                layout = FlowLayout(FlowLayout.CENTER, 5, 5)
                add(JLabel("To see bytecode, invoke"))
                add(JLabel(ShowBytecodeAction.ICON))
                add(JLabel(ActionManager.getInstance().getAction("ShowByteCodeJclasslib").templateText))
                add(JLabel("while in an editor or on a class in the project window"))
            })
            add(Box.createVerticalGlue())
        }, BorderLayout.CENTER)
    }
    val contentManager = toolWindow.contentManager
    contentManager.addContent(contentManager.factory.createContent(infoPanel, null, false).apply {
        isCloseable = false
    })
}

class ByteCodeToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        contentManager.addContentManagerListener(object : ContentManagerListener {
            override fun contentAdded(e: ContentManagerEvent) {
                toolWindow.isAvailable = true
            }

            override fun contentRemoved(e: ContentManagerEvent) {
                toolWindow.isAvailable = contentManager.contentCount > 0
            }
        })
    }

    override fun init(toolWindow: ToolWindow) {
        super.init(toolWindow)
        addInfoPanel(toolWindow)
        val contentManager = toolWindow.contentManager
        contentManager.addContentManagerListener(object : ContentManagerListener {
            override fun contentRemoved(event: ContentManagerEvent) {
                if (contentManager.contents.isEmpty()) {
                    addInfoPanel(toolWindow)
                }
            }
        })
    }
}

private const val MODIFICATION_PREFIX = "* "

class BytecodeToolWindowPanel(override var classFile: ClassFile, val locatedClassFile: LocatedClassFile, val project: Project) : SimpleToolWindowPanel(true, true), BrowserServices {

    lateinit var content: Content

    private val closeAction: AnAction = object : CloseTabToolbarAction() {
        override fun actionPerformed(e: AnActionEvent) {
            content.manager?.removeContent(content, true)
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

        override fun getActionUpdateThread() = ActionUpdateThread.EDT
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

        override fun getActionUpdateThread() = ActionUpdateThread.EDT
    }

    private val reloadAction: AnAction = object : DumbAwareAction() {
        init {
            templatePresentation.apply {
                icon = PlatformIcons.SYNCHRONIZE_ICON
                text = "Reload"
            }
        }

        override fun actionPerformed(e: AnActionEvent) {
            if (browserComponent.canRemove()) {
                object : Task.Backgroundable(project, "Reloading class file", false) {
                    var newClassFile: ClassFile? = null
                    override fun run(indicator: ProgressIndicator) {
                        newClassFile = readClassFile(locatedClassFile, project)
                    }

                    override fun onSuccess() {
                        newClassFile?.let { newClassFile ->
                            classFile = newClassFile
                            browserComponent.rebuild()
                        }
                        resetModified()
                    }
                }
                .queue()
            }
        }

        override fun getActionUpdateThread() = ActionUpdateThread.EDT
    }

    private val saveAction: AnAction = object : DumbAwareAction() {
        init {
            templatePresentation.apply {
                icon = AllIcons.Actions.MenuSaveall
                text = "Save Modified Class File"
            }
        }

        override fun actionPerformed(e: AnActionEvent) {
            saveModified()
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = browserComponent.isModified
        }

        override fun getActionUpdateThread() = ActionUpdateThread.EDT
    }

    private val webAction: AnAction = object : DumbAwareAction() {
        init {
            templatePresentation.apply {
                icon = PlatformIcons.WEB_ICON
                text = "Show Web Site"
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
                showWarningDialog("Class $className could not be found", project)
            }
        }
    }

    private fun getRoot(): VirtualFile {
        var root = locatedClassFile.virtualFile
        repeat(classFile.thisClassName.count { it == '/' } + 1) {
            root = root.parent
        }
        return root
    }

    override fun canOpenClassFiles(): Boolean = true
    override fun canSaveClassFiles(): Boolean = locatedClassFile.writableUrl != null

    override fun showURL(urlSpec: String) {
        BrowserUtil.browse(urlSpec)
    }

    override fun modified() {
        content.displayName = MODIFICATION_PREFIX + content.displayName
    }

    init {
        val actionGroup = DefaultActionGroup().apply {
            add(closeAction)
            add(backwardActionDelegate)
            add(forwardActionDelegate)
            add(reloadAction)
            add(saveAction)
            addSeparator()
            add(webAction)
        }
        val actionToolbar = ActionManager.getInstance().createActionToolbar("bytecodeToolBar", actionGroup, true).apply {
            targetComponent = this@BytecodeToolWindowPanel
        }
        toolbar = actionToolbar.component
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

    private fun saveModified() {
        if (writeClassFile(classFile, requireNotNull(locatedClassFile.writableUrl), getParentWindow(), null) {
            FileChooser.chooseFile(FileChooserDescriptor(false, true, false, false, false, false).apply {
                title = "Select Directory"
                description = "Select the output directory for the modified class files"
            }, project, null)?.let {
                File(it.path)
            }
        }) {
            resetModified()
        }
    }

    private fun resetModified() {
        browserComponent.isModified = false
        content.displayName = content.displayName.removePrefix(MODIFICATION_PREFIX)
    }

    companion object {
        init {
            initUiFacades()
        }
    }
}
