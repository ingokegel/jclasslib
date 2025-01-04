/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.KeyValueDetailPane
import org.gjt.jclasslib.browser.detail.constants.DelegateBuilder
import org.gjt.jclasslib.browser.detail.constants.DelegatesEditor
import org.gjt.jclasslib.structures.attributes.RecordAttribute
import org.gjt.jclasslib.structures.attributes.RecordEntry

class RecordAttributeDetailPane(services: BrowserServices) : KeyValueDetailPane<RecordAttribute>(RecordAttribute::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.record.entries.count")) { recordAttribute -> recordAttribute.entries.size.toString() }
    }
}

class RecordEntryDetailPane(services: BrowserServices) : KeyValueDetailPane<RecordEntry>(RecordEntry::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.name")) { entry -> entry.nameIndex }
        addConstantPoolLink(getString("key.descriptor")) { entry -> entry.descriptorIndex }
        addEditor { RecordEntryEditor() }
    }

    override fun hasInsets() = true

    inner class RecordEntryEditor : DelegatesEditor<RecordEntry>() {
        override fun DelegateBuilder<RecordEntry>.buildDelegateSpecs() {
            addDelegateSpec(getString("menu.name")) {
                services.classFile.getConstantPoolUtf8Entry(it.nameIndex)
            }
            addDelegateSpec(getString("menu.descriptor")) {
                services.classFile.getConstantPoolUtf8Entry(it.descriptorIndex)
            }
        }
    }
}
