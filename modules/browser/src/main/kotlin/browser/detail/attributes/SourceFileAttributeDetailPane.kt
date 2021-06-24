/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.KeyValueDetailPane
import org.gjt.jclasslib.browser.detail.constants.DelegateBuilder
import org.gjt.jclasslib.browser.detail.constants.DelegatesEditor
import org.gjt.jclasslib.structures.attributes.SourceFileAttribute

class SourceFileAttributeDetailPane(services: BrowserServices) : KeyValueDetailPane<SourceFileAttribute>(SourceFileAttribute::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.source.file.name.index"), SourceFileAttribute::sourceFileIndex)
        addEditor { SourceFileEditor() }
    }

    class SourceFileEditor : DelegatesEditor<SourceFileAttribute>() {
        override fun DelegateBuilder<SourceFileAttribute>.buildDelegateSpecs() {
            addDelegateSpec {
                it.sourceFileConstant
            }
        }
    }
}

