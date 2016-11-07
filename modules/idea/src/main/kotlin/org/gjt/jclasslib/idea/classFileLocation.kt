/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.byteCodeViewer.ByteCodeViewerManager
import com.intellij.ide.util.JavaAnonymousClassesHelper
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
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
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiAnonymousClass
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.ClassUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.PsiUtilCore
import org.gjt.jclasslib.browser.config.BrowserPath
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

val LOG: Logger = Logger.getInstance("#jclasslib")

fun openClassFile(psiElement: PsiElement, browserPath: BrowserPath?, project: Project) {
    ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Locating class file ...") {
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
        }

        override fun onSuccess() {
            if (!project.isDisposed && errorMessage != null && myTitle != null) {
                Messages.showWarningDialog(project, errorMessage, "jclasslib bytecode viewer")
            } else {
                showClassFile(virtualFile!!, browserPath, project)
            }
        }
    })
}

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

private fun getClassName(containingClass: PsiClass): String? {
    if (containingClass is PsiAnonymousClass) {
        val containingClassOfAnonymous = PsiTreeUtil.getParentOfType(containingClass, PsiClass::class.java) ?: return null
        return getClassName(containingClassOfAnonymous) + JavaAnonymousClassesHelper.getName(containingClass)
    } else {
        return ClassUtil.getJVMClassName(containingClass)
    }
}

