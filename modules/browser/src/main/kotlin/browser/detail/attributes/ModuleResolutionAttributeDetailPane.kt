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
import org.gjt.jclasslib.structures.attributes.ModuleResolutionAttribute
import org.gjt.jclasslib.structures.attributes.ModuleResolutionType

class ModuleResolutionAttributeDetailPane(services: BrowserServices) : KeyValueDetailPane<ModuleResolutionAttribute>(ModuleResolutionAttribute::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.resolution")) { attribute -> attribute.resolution.toString() }
        addEditor { ModuleResolutionEditor() }
    }

    class ModuleResolutionEditor : DelegatesEditor<ModuleResolutionAttribute>() {
        override fun DelegateBuilder<ModuleResolutionAttribute>.buildDelegateSpecs() {
            addEnumSpec(getString("menu.resolution"), ModuleResolutionType::class.java, ModuleResolutionAttribute::resolution)
        }
    }
}
