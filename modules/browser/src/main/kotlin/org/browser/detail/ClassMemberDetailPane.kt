/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
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
        addConstantPoolLink("Name:", ClassMember::nameIndex)
        addConstantPoolLink("Descriptor:", ClassMember::descriptorIndex)
        addDetail("Access flags:") { classMember -> "${classMember.formattedAccessFlags} [${classMember.accessFlagsVerbose}]" }
        super.addLabels()
    }

    override val signatureButtonText: String
        get() = "Copy signature to clipboard"
}

