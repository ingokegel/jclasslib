/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.constants.ConstantNameAndTypeInfo

class ConstantNameAndTypeInfoDetailPane(services: BrowserServices) : AbstractConstantInfoDetailPane<ConstantNameAndTypeInfo>(services) {
    override val constantClass: Class<ConstantNameAndTypeInfo>
        get() = ConstantNameAndTypeInfo::class.java

    override fun addLabels() {
        addConstantPoolLink("Name:") { constant -> constant.nameIndex }
        addConstantPoolLink("Descriptor:") { constant -> constant.descriptorIndex }
    }
}

