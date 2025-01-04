/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.constants.DelegateBuilder
import org.gjt.jclasslib.browser.detail.constants.DelegatesEditor
import org.gjt.jclasslib.structures.ClassMember

class ClassMemberDetailPane(services: BrowserServices, signatureMode: SignatureMode) :
        FixedListWithSignatureDetailPane<ClassMember>(ClassMember::class.java, services, signatureMode) {

    override val signatureVerbose: String
        get() = StringBuilder().apply {
            element?.let {
                appendSignature(it, signatureMode)
            }
        }.toString()

    override fun addLabels() {
        addConstantPoolLink(getString("key.name"), ClassMember::nameIndex)
        addConstantPoolLink(getString("key.descriptor"), ClassMember::descriptorIndex)
        addDetail(getString("key.access.flags")) { classMember -> "${classMember.formattedAccessFlags} [${classMember.accessFlagsVerbose}]" }
        addEditor { ClassMemberEditor() }
        super.addLabels()
    }

    override val signatureButtonText: String
        get() = getString("action.copy.signature")

    inner class ClassMemberEditor : DelegatesEditor<ClassMember>() {
        override fun DelegateBuilder<ClassMember>.buildDelegateSpecs() {
            addDelegateSpec(getString("menu.name")) {
                it.nameConstant
            }
            addDelegateSpec(getString("menu.descriptor")) {
                it.descriptorConstant
            }
            addAccessFlagsSpec(getString("menu.access.flags"), signatureMode.getAccessFlags(), ClassMember::accessFlags)
        }
    }
}

