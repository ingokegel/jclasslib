/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.FlagsEditDialog
import org.gjt.jclasslib.browser.detail.KeyValueDetailPane
import org.gjt.jclasslib.browser.detail.constants.DelegateBuilder
import org.gjt.jclasslib.browser.detail.constants.DelegatesEditor
import org.gjt.jclasslib.structures.attributes.ModuleResolutionAttribute
import org.gjt.jclasslib.structures.attributes.ModuleResolutionType
import java.awt.Window

class ModuleResolutionAttributeDetailPane(services: BrowserServices) : KeyValueDetailPane<ModuleResolutionAttribute>(ModuleResolutionAttribute::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.resolution")) { attribute -> "${attribute.formattedResolution} [${attribute.resolutionVerbose}]" }
        addEditor { ModuleResolutionEditor() }
    }

    class ModuleResolutionEditor : DelegatesEditor<ModuleResolutionAttribute>() {
        override fun DelegateBuilder<ModuleResolutionAttribute>.buildDelegateSpecs() {
            addFlagsSpec(getString("menu.resolution"), ModuleResolutionType.entries.toSet(), ::ModuleResolutionEditDialog, ModuleResolutionAttribute::resolution)
        }
    }

    class ModuleResolutionEditDialog(selectedValue: Int, validFlags: Set<ModuleResolutionType>, parentWindow: Window?, delegateName: String?)
        : FlagsEditDialog<ModuleResolutionType>(selectedValue, validFlags, parentWindow, delegateName
    ) {
        override fun getFlagText(flag: ModuleResolutionType) = flag.verbose

        override fun composeFrom(keys: Set<ModuleResolutionType>): Int = ModuleResolutionType.composeFrom(keys)
        override fun decompose(selectedValue: Int, validFlags: Set<ModuleResolutionType>): List<ModuleResolutionType> {
            return ModuleResolutionType.decompose(selectedValue, validFlags)
        }
    }
}
