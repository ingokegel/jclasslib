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
import org.gjt.jclasslib.structures.attributes.EnclosingMethodAttribute

class EnclosingMethodAttributeDetailPane(services: BrowserServices) : KeyValueDetailPane<EnclosingMethodAttribute>(EnclosingMethodAttribute::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.class.index"), EnclosingMethodAttribute::classInfoIndex)
        addConstantPoolLink(getString("key.method.index"), EnclosingMethodAttribute::methodInfoIndex)
        addEditor { EnclosingMethodEditor() }
    }

    class EnclosingMethodEditor : DelegatesEditor<EnclosingMethodAttribute>() {
        override fun DelegateBuilder<EnclosingMethodAttribute>.buildDelegateSpecs() {
            addDelegateSpec(getString("menu.class")) {
                it.classInfoConstant
            }
            addDelegateSpec(getString("menu.method.name")) {
                it.methodInfoConstant.nameConstant
            }
            addDelegateSpec(getString("menu.descriptor")) {
                it.methodInfoConstant.descriptorConstant
            }
        }
    }
}
