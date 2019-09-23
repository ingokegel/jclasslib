/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.byteCodeViewer.ByteCodeViewerManager
import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.util.JavaAnonymousClassesHelper
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiAnonymousClass
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiTypeParameter
import com.intellij.psi.util.ClassUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import org.gjt.jclasslib.browser.config.BrowserPath
import java.io.FileNotFoundException

private class LocationResult private constructor(val locatedClassFile: LocatedClassFile?, val errorMessage: String?) {
    companion object {
        fun of(locatedClassFile: LocatedClassFile) = LocationResult(locatedClassFile, null)
        fun of(errorMessage: String) = LocationResult(null, errorMessage)
    }
}

data class LocatedClassFile(val jvmClassName: String, val virtualFile: VirtualFile)

fun isContainedInClass(psiElement: PsiElement) : Boolean {
    val containingClass = getContainingClass(psiElement)
    return if (containingClass != null) {
        getJVMClassName(containingClass) != null
    } else {
        false
    }
}

fun openClassFile(psiElement: PsiElement, browserPath: BrowserPath?, project: Project) {
    ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Locating class file ...") {
        var locationResult: LocationResult? = null

        override fun run(indicator: ProgressIndicator) {
            locationResult = ApplicationManager.getApplication().runReadAction(Computable<LocationResult> {
                try {
                    locateClassFile(psiElement)
                } catch (e: Exception) {
                    LocationResult.of("Class file could not be found" + (if (e.message.isNullOrBlank()) "" else ": " + e.message))
                }
            })
        }

        override fun onSuccess() {
            val locatedClassFile = locationResult?.locatedClassFile
            if (locatedClassFile != null) {
                showClassFile(locatedClassFile, browserPath, project)
            } else {
                if (!project.isDisposed && myTitle != null) {
                    Messages.showWarningDialog(
                        project,
                        locationResult?.errorMessage ?: "internal error",
                        "jclasslib bytecode viewer"
                    )
                }
            }
        }
    })
}

private fun locateClassFile(psiElement: PsiElement): LocationResult {
    val containingClass = getContainingClass(psiElement) ?: throw FileNotFoundException("<containing class>")
    val jvmClassName = getJVMClassName(containingClass) ?: throw FileNotFoundException("<class name>")
    val virtualFile = getFileClass(containingClass).originalElement.containingFile.virtualFile
    return LocationResult.of(LocatedClassFile(jvmClassName, virtualFile))
}

private tailrec fun getFileClass(c: PsiClass): PsiClass =
    if (!PsiUtil.isLocalOrAnonymousClass(c)) {
        c
    } else {
        val containingClass = PsiTreeUtil.getParentOfType(c, PsiClass::class.java)
        if (containingClass == null) {
            c
        } else {
            getFileClass(containingClass)
        }
    }

private fun getContainingClass(psiElement: PsiElement): PsiClass? {
    val byteCodeViewerPlugin = PluginManager.getPlugin(PluginId.getId("ByteCodeViewer"))
    return if (byteCodeViewerPlugin != null && byteCodeViewerPlugin.isEnabled) {
        ByteCodeViewerManager.getContainingClass(psiElement)
    } else {
        val containingClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass::class.java, false)
        if (containingClass is PsiTypeParameter) {
            getContainingClass(containingClass)
        } else {
            containingClass
        }
    }
}

private fun getJVMClassName(containingClass: PsiClass): String? {
    return if (containingClass is PsiAnonymousClass) {
        val containingClassOfAnonymous = PsiTreeUtil.getParentOfType(containingClass, PsiClass::class.java) ?: return null
        getJVMClassName(containingClassOfAnonymous) + JavaAnonymousClassesHelper.getName(containingClass)
    } else {
        ClassUtil.getJVMClassName(containingClass)
    }
}
