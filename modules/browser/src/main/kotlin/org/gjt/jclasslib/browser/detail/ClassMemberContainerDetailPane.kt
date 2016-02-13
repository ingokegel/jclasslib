/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.ClassMember

class ClassMemberContainerDetailPane(services: BrowserServices, signatureMode: FixedListWithSignatureDetailPane.SignatureMode) :
        FixedListWithSignatureDetailPane<Array<out ClassMember>>(Array<out ClassMember>::class.java, services, signatureMode) {

    override val signatureVerbose: String
        get() = StringBuilder().apply {
            for (classMember in element ?: arrayOf()) {
                appendSignature(classMember, signatureMode)
                append('\n')
            }

        }.toString()

    override fun addLabels() {
        addDetail("Member count:") { classMembers -> classMembers.size.toString() }
        super.addLabels()
    }

    override val signatureButtonText: String
        get() = "Copy signatures to clipboard"
}

