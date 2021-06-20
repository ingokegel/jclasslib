/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute
import org.gjt.jclasslib.structures.constants.*
import org.gjt.jclasslib.util.GUIHelper.getParentWindow
import org.jetbrains.annotations.Nls
import javax.swing.JButton
import javax.swing.JOptionPane

abstract class ConstantDetailPane<T : Constant>(constantClass: Class<T>, services: BrowserServices) : KeyValueDetailPane<T>(constantClass, services) {
    protected fun addClassElementOpener() {
        addClassElementOpener { constant -> constant }
    }

    override fun hasInsets() = true
}

abstract class ConstantNameInfoDetailPane<T : ConstantNameInfo>(constantClass: Class<T>, services: BrowserServices) : ConstantDetailPane<T>(constantClass, services) {
    override fun addLabels() {
        addConstantPoolLink(getTargetName(), ConstantNameInfo::nameIndex)
    }

    @Nls
    protected abstract fun getTargetName(): String
}

class ConstantClassInfoDetailPane(services: BrowserServices) : ConstantNameInfoDetailPane<ConstantClassInfo>(ConstantClassInfo::class.java, services) {
    override fun addLabels() {
        super.addLabels()
        addClassElementOpener()
    }

    override fun getTargetName() = getString("key.class.name")
}

class ConstantModuleInfoDetailPane(services: BrowserServices) : ConstantNameInfoDetailPane<ConstantModuleInfo>(ConstantModuleInfo::class.java, services) {
    override fun getTargetName() = getString("key.module.name")
}

class ConstantPackageInfoDetailPane(services: BrowserServices) : ConstantNameInfoDetailPane<ConstantPackageInfo>(ConstantPackageInfo::class.java, services) {
    override fun getTargetName() = getString("key.package.name")
}

class ConstantReferenceDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantReference>(ConstantReference::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.class.name"), ConstantReference::classIndex)
        addConstantPoolLink(getString("key.name.and.type"), ConstantReference::nameAndTypeIndex)
        addClassElementOpener()
    }
}

class ConstantStringInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantStringInfo>(ConstantStringInfo::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.string"), ConstantStringInfo::stringIndex)
    }
}

class ConstantIntegerInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantIntegerInfo>(ConstantIntegerInfo::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.bytes"), ConstantIntegerInfo::formattedBytes)
        addDetail(getString("key.integer")) { constant -> constant.int.toString() }
    }
}

class ConstantFloatInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantFloatInfo>(ConstantFloatInfo::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.bytes"), ConstantFloatInfo::formattedBytes)
        addDetail(getString("key.float")) { constant -> constant.float.toString() }
    }
}

class ConstantLongInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantLongInfo>(ConstantLongInfo::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.high.bytes"), ConstantLongInfo::formattedHighBytes)
        addDetail(getString("key.low.bytes"), ConstantLongInfo::formattedLowBytes)
        addDetail(getString("key.long")) { constant -> constant.long.toString() }
    }
}

class ConstantDoubleInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantDoubleInfo>(ConstantDoubleInfo::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.high.bytes"), ConstantDoubleInfo::formattedHighBytes)
        addDetail(getString("key.low.bytes"), ConstantDoubleInfo::formattedLowBytes)
        addDetail(getString("key.double")) { constant -> constant.double.toString() }
    }
}

class ConstantNameAndTypeInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantNameAndTypeInfo>(ConstantNameAndTypeInfo::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.name")) { constant -> constant.nameIndex }
        addConstantPoolLink(getString("key.descriptor")) { constant -> constant.descriptorIndex }
    }
}

class ConstantMethodTypeDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantMethodTypeInfo>(ConstantMethodTypeInfo::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.type"), ConstantMethodTypeInfo::descriptorIndex)
    }
}

class ConstantMethodHandleInfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantMethodHandleInfo>(ConstantMethodHandleInfo::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.reference.kind")) { constant -> constant.type.verbose }
        addConstantPoolLink(getString("key.reference.index")) { constant -> constant.referenceIndex }
    }
}

class ConstantDynamicDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantDynamic>(ConstantDynamic::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.name.and.type")) { constant -> constant.nameAndTypeIndex }
        addAttributeLink(getString("key.bootstrap.method"), BootstrapMethodsAttribute::class.java, "BootstrapMethods #") { constant -> constant.bootstrapMethodAttributeIndex }
    }
}

class ConstantUtf8InfoDetailPane(services: BrowserServices) : ConstantDetailPane<ConstantUtf8Info>(ConstantUtf8Info::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.byte.array.length")) { constant -> constant.bytes.size.toString() }
        addDetail(getString("key.string.length")) { constant -> constant.string.length.toString() }
        addDetail(getString("key.string")) { constant ->
            try {
                constant.verbose
            } catch (e: InvalidByteCodeException) {
                getString("message.invalid.constant.pool.entry")
            }
        }
        add(StringEditor(this), "newline, spanx")
    }
}

class StringEditor(detailPane: ConstantUtf8InfoDetailPane): JButton(getString("action.edit")) {

    private var constant: ConstantUtf8Info? = null
    init {
        detailPane.addShowHandler {
            constant = it
        }
        addActionListener {
            constant?.let { constant ->
                val value = constant.string
                val newValue = JOptionPane.showInputDialog(getParentWindow(), getString("key.edit.string.value"), getString("input.edit.string.title"), JOptionPane.QUESTION_MESSAGE, null, null, value) as String
                if (value != newValue) {
                    constant.string = newValue
                    detailPane.modified()
                }
            }
        }
    }
}