/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.constants.DelegateBuilder
import org.gjt.jclasslib.browser.detail.constants.DelegatesEditor
import org.gjt.jclasslib.structures.AccessFlag
import org.gjt.jclasslib.structures.ClassFile

class GeneralDetailPane(services: BrowserServices) : KeyValueDetailPane<ClassFile>(ClassFile::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.minor.version")) { classFile -> classFile.minorVersion.toString() }
        addDetail(getString("key.major.version")) { classFile -> "${classFile.majorVersion} [${classFile.majorVersionVerbose}]" }
        addDetail(getString("key.constant.pool.count")) { classFile -> classFile.constantPool.size.toString() }
        addDetail(getString("key.access.flags")) { classFile -> "${classFile.formattedAccessFlags} [${classFile.accessFlagsVerbose}]" }
        addConstantPoolLink(getString("key.this.class"), ClassFile::thisClass)
        addConstantPoolLink(getString("key.super.class"), ClassFile::superClass)
        addDetail(getString("key.interfaces.count")) { classFile -> classFile.interfaces.size.toString() }
        addDetail(getString("key.fields.count")) { classFile -> classFile.fields.size.toString() }
        addDetail(getString("key.methods.count")) { classFile -> classFile.methods.size.toString() }
        addDetail(getString("key.attributes.count")) { classFile -> classFile.attributes.size.toString() }
        addEditor { GeneralDetailEditor() }
    }

    override fun hasInsets() = true

    class GeneralDetailEditor: DelegatesEditor<ClassFile>() {
        override fun DelegateBuilder<ClassFile>.buildDelegateSpecs() {
            addIntSpec(getString("menu.minor.version"), ClassFile::minorVersion)
            addIntSpec(getString("menu.major.version"), ClassFile::majorVersion)
            addAccessFlagsSpec(getString("menu.access.flags"), AccessFlag.CLASS_ACCESS_FLAGS, ClassFile::accessFlags)
            addDelegateSpec(getString("menu.this.class")) {
                it.thisClassConstant
            }
            addDelegateSpec(getString("menu.super.class")) {
                it.superClassConstant
            }
        }
    }
}

