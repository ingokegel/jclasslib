/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.usages

import org.gjt.jclasslib.browser.BrowserComponent
import org.gjt.jclasslib.structures.ClassMember
import org.gjt.jclasslib.structures.FieldInfo
import org.gjt.jclasslib.structures.MethodInfo
import org.gjt.jclasslib.structures.constants.ConstantFieldrefInfo
import org.gjt.jclasslib.structures.constants.ConstantInterfaceMethodrefInfo
import org.gjt.jclasslib.structures.constants.ConstantMethodrefInfo
import org.gjt.jclasslib.util.getParentWindow

private val JDK_PREFIXES = listOf("java/", "javax/", "jdk/", "sun/", "com/sun/")

fun findClassMemberUsages(browserComponent: BrowserComponent, classMember: ClassMember) {
    val className = browserComponent.services.classFile.thisClassName
    val classMemberName = classMember.name
    val classMemberDescriptor = classMember.descriptor
    val includeJdk = JDK_PREFIXES.any { className.startsWith(it) }

    val classUsages = findClassUsages(browserComponent.services, includeJdk, browserComponent.getParentWindow()) { constant ->
        if ((classMember is FieldInfo && constant is ConstantFieldrefInfo) ||
            (classMember is MethodInfo && (constant is ConstantMethodrefInfo || constant is ConstantInterfaceMethodrefInfo))
        ) {
            val nameAndTypeConstant = constant.nameAndTypeConstant
            constant.classConstant.name == className &&
                    nameAndTypeConstant.name == classMemberName &&
                    nameAndTypeConstant.descriptor == classMemberDescriptor
        } else {
            false
        }
    }
    if (classUsages.isNotEmpty()) {
        val classFile = browserComponent.services.classFile
        if (classUsages.size == 1 && classUsages.first().className == classFile.thisClassName) {
            findConstantUsages(browserComponent, classFile.constantPool[classUsages.first().referenceIndex])
        } else {
            showClassUsages(classUsages, browserComponent.services, browserComponent.getParentWindow())
        }
    } else {
        showNoUsagesFoundMessage(browserComponent)
    }
}
