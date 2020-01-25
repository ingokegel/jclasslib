/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilBase

class ShowBytecodeAction : AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.apply {
            isEnabled = getPsiElement(e)?.run { containingFile is PsiClassOwner && isContainedInClass(this) } == true
            icon = ICON
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = getPsiElement(e) ?: return
        val project = e.project ?: return

        openClassFile(psiElement, null, project)
    }

    private fun getPsiElement(e: AnActionEvent): PsiElement? =
            getPsiElement(e.dataContext, e.project, e.getData(CommonDataKeys.EDITOR))

    private fun getPsiElement(dataContext: DataContext, project: Project?, editor: Editor?): PsiElement? = when {
        project == null -> null
        editor == null -> dataContext.getData(CommonDataKeys.PSI_ELEMENT)
        else -> {
            val psiFile = PsiUtilBase.getPsiFileInEditor(editor, project)
            psiFile?.let {
                val offset = editor.caretModel.offset
                InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, offset)
                        ?: psiFile.findElementAt(offset)
            }
        }
    }

    companion object {
        val ICON = IconLoader.getIcon("/icons/jclasslib.png") // 13x13
    }
}