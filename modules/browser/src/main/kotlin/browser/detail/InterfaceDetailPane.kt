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
import org.gjt.jclasslib.structures.constants.ConstantClassInfo

class InterfaceDetailPane(services: BrowserServices) : KeyValueDetailPane<Int>(Int::class.javaObjectType, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.interface")) { index -> index }
        addEditor { InterfaceEditor() }
    }

    override fun hasInsets() = true

    inner class InterfaceEditor : DelegatesEditor<Int>() {
        override fun DelegateBuilder<Int>.buildDelegateSpecs() {
            addDelegateSpec {
                services.classFile.getConstantPoolEntry(it, ConstantClassInfo::class.java)
            }
        }
    }
}
