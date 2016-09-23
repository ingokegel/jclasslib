/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.KeyValueDetailPane
import org.gjt.jclasslib.structures.AttributeInfo

class GenericAttributeDetailPane(services: BrowserServices) : KeyValueDetailPane<AttributeInfo>(AttributeInfo::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Attribute name index:", AttributeInfo::attributeNameIndex)
        addDetail("Attribute length:") { attribute -> attribute.getAttributeLength().toString() }
    }
}

