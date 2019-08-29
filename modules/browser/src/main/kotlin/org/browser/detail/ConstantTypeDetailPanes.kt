/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute
import org.gjt.jclasslib.structures.constants.*

abstract class ConstantDetailPane<T : Constant>(constantClass: Class<T>, services: BrowserServices) : KeyValueDetailPane<T>(constantClass, services) {
    protected fun addClassElementOpener() {
        addClassElementOpener { constant -> constant }
    }

    override fun hasInsets() = true
}

abstract class ConstantNameInfoDetailPane<T : ConstantNameInfo>(constantClass: Class<T>, services: BrowserServices) : ConstantDetailPane<T>(constantClass, services) {
    override fun addLabels() {
        addConstantPoolLink(getTargetName() + " name:", ConstantNameInfo::nameIndex)
    }

    protected abstract fun getTargetName(): String
}

class ConstantClassInfoDetailPane(services: BrowserServices) : ConstantNameInfoDetailPane<ConstantClassInfo>(ConstantClassInfo::class.java, services) {
    override fun addLabels() {
        super.addLabels()
        addClassElementOpener()
    }

    override fun getTargetName() = "Class"
}

class ConstantModuleInfoDetailPane(services: BrowserServices) : ConstantNameInfoDetailPane<ConstantModuleInfo>(ConstantModuleInfo::class.java, services) {
    override fun getTargetName() = "Module"
}

class ConstantPackageInfoDetailPane(services: BrowserServices) : ConstantNameInfoDetailPane<ConstantPackageInfo>(ConstantPackageInfo::class.java, services) {
    override fun getTargetName() = "Package"
}

class ConstantReferenceDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantReference>(ConstantReference::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Class name:", ConstantReference::classIndex)
        addConstantPoolLink("Name and type:", ConstantReference::nameAndTypeIndex)
        addClassElementOpener()
    }
}

class ConstantStringInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantStringInfo>(ConstantStringInfo::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("String:", ConstantStringInfo::stringIndex)
    }
}

class ConstantIntegerInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantIntegerInfo>(ConstantIntegerInfo::class.java, services) {
    override fun addLabels() {
        addDetail("Bytes:", ConstantIntegerInfo::formattedBytes)
        addDetail("Integer:") { constant -> constant.int.toString() }
    }
}

class ConstantFloatInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantFloatInfo>(ConstantFloatInfo::class.java, services) {
    override fun addLabels() {
        addDetail("Bytes:", ConstantFloatInfo::formattedBytes)
        addDetail("Float:") { constant -> constant.float.toString() }
    }
}

class ConstantLongInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantLongInfo>(ConstantLongInfo::class.java, services) {
    override fun addLabels() {
        addDetail("High bytes:", ConstantLongInfo::formattedHighBytes)
        addDetail("Low bytes:", ConstantLongInfo::formattedLowBytes)
        addDetail("Long") { constant -> constant.long.toString() }
    }
}

class ConstantDoubleInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantDoubleInfo>(ConstantDoubleInfo::class.java, services) {
    override fun addLabels() {
        addDetail("High bytes:", ConstantDoubleInfo::formattedHighBytes)
        addDetail("Low bytes:", ConstantDoubleInfo::formattedLowBytes)
        addDetail("Double") { constant -> constant.double.toString() }
    }
}

class ConstantNameAndTypeInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantNameAndTypeInfo>(ConstantNameAndTypeInfo::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Name:") { constant -> constant.nameIndex }
        addConstantPoolLink("Descriptor:") { constant -> constant.descriptorIndex }
    }
}

class ConstantMethodTypeDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantMethodTypeInfo>(ConstantMethodTypeInfo::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Type:", ConstantMethodTypeInfo::descriptorIndex)
    }
}

class ConstantMethodHandleInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantMethodHandleInfo>(ConstantMethodHandleInfo::class.java, services) {
    override fun addLabels() {
        addDetail("Reference kind:") { constant -> constant.type.verbose }
        addConstantPoolLink("Reference index :") { constant -> constant.referenceIndex }
    }
}

class ConstantDynamicDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantDynamic>(ConstantDynamic::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Name and type:") { constant -> constant.nameAndTypeIndex }
        addAttributeLink("Bootstrap method:", BootstrapMethodsAttribute::class.java, "BootstrapMethods #") { constant -> constant.bootstrapMethodAttributeIndex }
    }
}

class ConstantUtf8InfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantUtf8Info>(ConstantUtf8Info::class.java, services) {
    override fun addLabels() {
        addDetail("Length of byte array:") { constant -> constant.bytes.size.toString() }
        addDetail("Length of string:") { constant -> constant.string.length.toString() }
        addDetail("String") { constant ->
            try {
                constant.verbose
            } catch (e: InvalidByteCodeException) {
                "invalid constant pool entry"
            }
        }
    }
}
