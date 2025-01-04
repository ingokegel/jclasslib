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
import org.gjt.jclasslib.structures.attributes.ModuleMainClassAttribute

class ModuleMainClassAttributeDetailPane(services: BrowserServices) : KeyValueDetailPane<ModuleMainClassAttribute>(ModuleMainClassAttribute::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.main.class"), ModuleMainClassAttribute::mainClassIndex)
        addEditor { ModuleMainClassEditor() }
    }

    class ModuleMainClassEditor : DelegatesEditor<ModuleMainClassAttribute>() {
        override fun DelegateBuilder<ModuleMainClassAttribute>.buildDelegateSpecs() {
            addDelegateSpec {
                it.mainClassConstant
            }
        }
    }
}
