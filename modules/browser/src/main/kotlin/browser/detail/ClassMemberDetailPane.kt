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
import org.gjt.jclasslib.browser.usages.findClassMemberUsages
import org.gjt.jclasslib.structures.ClassMember
import javax.swing.JButton

class ClassMemberDetailPane(services: BrowserServices, signatureMode: SignatureMode) :
        FixedListWithSignatureDetailPane<ClassMember>(ClassMember::class.java, services, signatureMode) {

    private val btnFindUsages: JButton = JButton(getString("action.find.usages")).apply {
        addActionListener {
            element?.let {classMember ->
                findClassMemberUsages(services.browserComponent, classMember)
            }
        }
    }

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
        addCommon()
    }

    override fun addEditor(editorProvider: () -> DataEditor<ClassMember>) {
        super.addEditor(editorProvider)
        if (services.canScanClassFiles()) {
            add(btnFindUsages, "newline, spanx")
        }
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

