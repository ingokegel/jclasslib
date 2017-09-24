/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.codegen.binding.CodegenBinding
import org.jetbrains.kotlin.fileClasses.NoResolveFileClassesProvider
import org.jetbrains.kotlin.fileClasses.getFileClassInternalName
import org.jetbrains.kotlin.idea.debugger.evaluate.KotlinDebuggerCaches
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.org.objectweb.asm.Type
import java.util.*

class KotlinClassNameProvider {

    private val cache = WeakHashMap<PsiElement, String>()

    companion object {
        fun getInstance(project: Project): KotlinClassNameProvider {
            return ServiceManager.getService(project, KotlinClassNameProvider::class.java)
        }

        private val CLASS_ELEMENT_TYPES = arrayOf<Class<out PsiElement>>(
                KtFile::class.java,
                KtClassOrObject::class.java,
                KtProperty::class.java,
                KtNamedFunction::class.java,
                KtFunctionLiteral::class.java,
                KtAnonymousInitializer::class.java)

        private fun getRelevantElement(element: PsiElement): PsiElement? {
            @Suppress("LoopToCallChain")
            for (elementType in CLASS_ELEMENT_TYPES) {
                if (elementType.isInstance(element)) {
                    return element
                }
            }
            return runReadAction { PsiTreeUtil.getNonStrictParentOfType(element, *CLASS_ELEMENT_TYPES) }
        }
    }

    fun getClassName(element: PsiElement): String? {
        return try {
            val relevantElement = runReadAction { getRelevantElement(element) }
            if (relevantElement != null) {
                cache.getOrPut(relevantElement) {
                    getClassNameForRelevantElement(relevantElement)?.replace('/', '.')
                }
            } else {
                null
            }

        } catch(t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }

    @Suppress("NON_TAIL_RECURSIVE_CALL")
    internal tailrec fun getClassNameForRelevantElement(element: PsiElement?): String? {
        if (element == null) return null

        return when (element) {
            is KtFile -> {
                runReadAction { NoResolveFileClassesProvider.getFileClassInternalName(element) }.toJdiName()
            }
            is KtClassOrObject -> {
                runReadAction {
                    getNameForNonLocalClass(element)
                }
            }
            is KtNamedFunction -> {
                val typeMapper = KotlinDebuggerCaches.getOrCreateTypeMapper(element)
                val classNamesOfContainingDeclaration = getClassNameForRelevantElement(element.relevantParentInReadAction)
                if (runReadAction { element.name == null || element.isLocal }) {
                    CodegenBinding.asmTypeForAnonymousClass(typeMapper.bindingContext, element).internalName.toJdiName()
                } else {
                    classNamesOfContainingDeclaration
                }
            }
            is KtAnonymousInitializer -> {
                val initializerOwner = runReadAction { element.containingDeclaration }
                if (initializerOwner is KtObjectDeclaration && initializerOwner.isCompanionInReadAction) {
                    return getClassNameForRelevantElement(runReadAction { initializerOwner.containingClassOrObject })
                }
                getClassNameForRelevantElement(initializerOwner)
            }
            is KtFunctionLiteral -> {
                val typeMapper = KotlinDebuggerCaches.getOrCreateTypeMapper(element)
                runReadAction {
                    CodegenBinding.asmTypeForAnonymousClass(typeMapper.bindingContext, element).internalName.toJdiName()
                }
            }
            else -> getClassNameForRelevantElement(element.relevantParentInReadAction)
        }
    }

    private fun getNameForNonLocalClass(nonLocalClassOrObject: KtClassOrObject): String? {
        val typeMapper = KotlinDebuggerCaches.getOrCreateTypeMapper(nonLocalClassOrObject)
        val descriptor = typeMapper.bindingContext[BindingContext.CLASS, nonLocalClassOrObject] ?: return null

        val type = typeMapper.mapClass(descriptor)
        if (type.sort != Type.OBJECT) {
            return null
        }

        return type.className
    }

    private val KtObjectDeclaration.isCompanionInReadAction: Boolean
        get() = runReadAction { isCompanion() }

    private val PsiElement.relevantParentInReadAction
        get() = runReadAction { getRelevantElement(this.parent) }

    private fun String.toJdiName() = replace('/', '.')
}

private fun <T> runReadAction(action: () -> T): T {
    return ApplicationManager.getApplication().runReadAction<T>(action)
}

