/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import com.intellij.psi.util.PsiUtilBase

class ShowBytecodeAction : AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.apply {
            isEnabled = getPsiElement(e)?.run { containingFile is PsiClassOwner && getContainingClass(this) != null } ?: false
            icon = ICON
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = getPsiElement(e) ?: return
        val project = e.project ?: return

        openClassFile(psiElement, null, project)
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

    companion object {
        val ICON = IconLoader.getIcon("/icons/jclasslib.png") // 13x13
    }
}