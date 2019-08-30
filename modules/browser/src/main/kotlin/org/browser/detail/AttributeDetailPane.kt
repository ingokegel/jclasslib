/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.attributes.*
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.attributes.*

class AttributeDetailPane(services: BrowserServices) : MultiDetailPane<AttributeInfo>(AttributeInfo::class.java, services) {

    override fun addCards() {
        addCard(ConstantValueAttribute::class.java, ConstantValueAttributeDetailPane(services))
        addCard(CodeAttribute::class.java, CodeAttributeDetailPane(services))
        addCard(ExceptionsAttribute::class.java, ExceptionsAttributeDetailPane(services))
        addCard(InnerClassesAttribute::class.java, InnerClassesAttributeDetailPane(services))
        addCard(SourceDebugExtensionAttribute::class.java, SourceDebugExtensionAttributeDetailPane(services))
        addCard(SourceFileAttribute::class.java, SourceFileAttributeDetailPane(services))
        addCard(LineNumberTableAttribute::class.java, LineNumberTableAttributeDetailPane(services))
        addCard(LocalVariableTableAttribute::class.java, LocalVariableTableAttributeDetailPane(services))
        addCard(EnclosingMethodAttribute::class.java, EnclosingMethodAttributeDetailPane(services))
        addCard(SignatureAttribute::class.java, SignatureAttributeDetailPane(services))
        addCard(LocalVariableTypeTableAttribute::class.java, LocalVariableTypeTableAttributeDetailPane(services))
        addCard(RuntimeAnnotationsAttribute::class.java, RuntimeAnnotationsAttributeDetailPane(services))
        addCard(RuntimeTypeAnnotationsAttribute::class.java, RuntimeAnnotationsAttributeDetailPane(services))
        addCard(AnnotationDefaultAttribute::class.java, AnnotationDefaultAttributeDetailPane(services))
        addCard(BootstrapMethodsAttribute::class.java, BootstrapMethodsAttributeDetailPane(services))
        addCard(StackMapTableAttribute::class.java, StackMapTableAttributeDetailPane(services))
        addCard(MethodParametersAttribute::class.java, MethodParametersAttributeDetailPane(services))
        addCard(ModuleAttribute::class.java, ModuleAttributeDetailPane(services))
        addCard(ModuleMainClassAttribute::class.java, ModuleMainClassAttributeDetailPane(services))
        addCard(ModulePackagesAttribute::class.java, ModulePackagesAttributeDetailPane(services))
        addCard(ModuleTargetAttribute::class.java, ModuleTargetAttributeDetailPane(services))
        addCard(ModuleResolutionAttribute::class.java, ModuleResolutionAttributeDetailPane(services))
        addCard(ModuleHashesAttribute::class.java, ModuleHashesAttributeDetailPane(services))
        addCard(NestHostAttribute::class.java, NestHostAttributeDetailPane(services))
        addCard(NestMembersAttribute::class.java, NestMembersAttributeDetailPane(services))
    }

    override fun createGenericInfoPane() = GenericAttributeDetailPane(services)

}

