/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.byteCodeViewer.ByteCodeViewerManager
import com.intellij.ide.util.JavaAnonymousClassesHelper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import com.intellij.psi.util.*
import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.structures.ClassFile
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class ShowBytecodeAction : AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.apply {
            isEnabled = getPsiElement(e)?.run { containingFile is PsiClassOwner && ByteCodeViewerManager.getContainingClass(this) != null } ?: false
            icon = ICON
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = getPsiElement(e) ?: return
        val project = e.project ?: return

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Locating class file ...") {
            var classFile: ClassFile? = null
            var virtualFile: VirtualFile? = null
            var errorMessage: String? = null

            override fun run(indicator: ProgressIndicator) {
                virtualFile = ApplicationManager.getApplication().runReadAction(Computable<VirtualFile> {
                    try {
                        getClassFile(psiElement)
                    } catch(e: Exception) {
                        errorMessage = "Class file could not be found" + (if (e.message.isNullOrBlank()) "" else ": " + e.message)
                        null
                    }
                })
                try {
                    classFile = virtualFile?.readClassFile()
                } catch(e: Exception) {
                    errorMessage = "Error reading class file: " + e.message
                }
            }

            override fun onSuccess() {
                if (!project.isDisposed && errorMessage != null && myTitle != null) {
                    Messages.showWarningDialog(project, errorMessage, "jclasslib bytecode viewer")
                } else {
                    //Messages.showMessageDialog("Got class file " + classFile, "jclasslib", Messages.getInformationIcon())
                    virtualFile?.let { virtualFile ->
                        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(BytecodeContentManager.TOOL_WINDOW_ID)
                        val panel = BytecodeToolWindowPanel()
                        toolWindow.contentManager.apply {
                            val content = factory.createContent(panel, virtualFile.name, false)
                            panel.content = content
                            addContent(content)
                            setSelectedContent(content, true)
                        }
                        toolWindow.activate(null)
                    }

                    //TODO continue
                }
            }
        })
    }

    private fun getPsiElement(e: AnActionEvent): PsiElement? {
        return getPsiElement(e.dataContext, e.project, e.getData(CommonDataKeys.EDITOR))
    }

    private fun getPsiElement(dataContext: DataContext, project: Project?, editor: Editor?): PsiElement? {
        if (project == null) {
            return null
        } else if (editor == null) {
            return dataContext.getData(CommonDataKeys.PSI_ELEMENT)
        } else {
            val psiFile = PsiUtilBase.getPsiFileInEditor(editor, project)
            val injectedEditor = InjectedLanguageUtil.getEditorForInjectedLanguageNoCommit(editor, psiFile)
            return injectedEditor?.let { injectedEditor ->
                findElementInFile(PsiUtilBase.getPsiFileInEditor(injectedEditor, project), injectedEditor)
            } ?: findElementInFile(psiFile, editor)
        }
    }

    private fun findElementInFile(psiFile: PsiFile?, editor: Editor): PsiElement? = psiFile?.findElementAt(editor.caretModel.offset)

    private fun getClassFile(psiElement: PsiElement): VirtualFile {
        val containingClass = ByteCodeViewerManager.getContainingClass(psiElement) ?: throw FileNotFoundException("<containing class>")
        val classVMName = getClassName(containingClass) ?: throw FileNotFoundException("<class name>")
        val module = ModuleUtilCore.findModuleForPsiElement(psiElement)
        return if (module == null) {
            getClassFileNoModule(psiElement, containingClass, classVMName)
        } else {
            getClassFileModule(module, containingClass, classVMName)
        }
    }

    private fun getClassFileNoModule(psiElement: PsiElement, containingClass: PsiClass, classVMName: String): VirtualFile {
        val project = containingClass.project
        val qualifiedName = PsiUtil.getTopLevelClass(psiElement)?.qualifiedName ?: throw FileNotFoundException("<top level class>")
        JavaPsiFacade.getInstance(project).findClass(qualifiedName, psiElement.resolveScope)?.let { psiClass ->
            val virtualFile = PsiUtilCore.getVirtualFile(psiClass)
            val fileIndex = ProjectRootManager.getInstance(project).fileIndex
            if (virtualFile != null && fileIndex.isInLibraryClasses(virtualFile)) {
                try {
                    val rootForFile = fileIndex.getClassRootForFile(virtualFile)
                    if (rootForFile != null) {
                        return rootForFile.findFileByRelativePath("/" + classVMName.replace('.', '/') + ".class") ?: throw FileNotFoundException()
                    }
                } catch (e: IOException) {
                    LOG.error(e)
                }
            }
        }
        throw FileNotFoundException()
    }

    private fun getClassFileModule(module: Module, containingClass: PsiClass, classVMName: String): VirtualFile {
        val virtualFile = containingClass.containingFile.virtualFile ?: throw FileNotFoundException("<virtual file>")
        val moduleExtension = CompilerModuleExtension.getInstance(module) ?: throw FileNotFoundException("<module extension>")
        val file = File(if (ProjectRootManager.getInstance(module.project).fileIndex.isInTestSourceContent(virtualFile)) {
            val pathForTests = moduleExtension.compilerOutputPathForTests ?: throw FileNotFoundException("<compilerOutputPathForTests>")
            pathForTests.path
        } else {
            val compilerOutputPath = moduleExtension.compilerOutputPath ?: throw FileNotFoundException("<compilerOutputPath>")
            compilerOutputPath.path
        } + "/" + classVMName.replace('.', '/') + ".class")

        return if (file.exists()) {
            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
        } else {
            null
        } ?: throw FileNotFoundException(file.path)
    }

    private fun VirtualFile.readClassFile(): ClassFile = ClassFileReader.readFromInputStream(inputStream)

    private fun getClassName(containingClass: PsiClass): String? {
        if (containingClass is PsiAnonymousClass) {
            val containingClassOfAnonymous = PsiTreeUtil.getParentOfType(containingClass, PsiClass::class.java) ?: return null
            return getClassName(containingClassOfAnonymous) + JavaAnonymousClassesHelper.getName(containingClass)
        } else {
            return ClassUtil.getJVMClassName(containingClass)
        }
    }

    companion object {
        val ICON = IconLoader.getIcon("/icons/jclasslib.png") // 13x13
        private val LOG = Logger.getInstance("#" + ShowBytecodeAction::class.java.name)
    }
}