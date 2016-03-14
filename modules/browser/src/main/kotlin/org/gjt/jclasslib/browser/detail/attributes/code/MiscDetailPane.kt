/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.KeyValueDetailPane
import org.gjt.jclasslib.structures.attributes.CodeAttribute

class MiscDetailPane(services: BrowserServices) : KeyValueDetailPane<CodeAttribute>(CodeAttribute::class.java, services) {
    init {
        name = "Misc"
    }

    override fun addLabels() {
        addDetail("Minor version:") { attribute -> attribute.maxStack.toString() }
        addDetail("Maximum local variables:") { attribute -> attribute.maxLocals.toString() }
        addDetail("Code length:") { attribute -> attribute.code.size.toString() }
    }

    override fun hasInsets() = true
}

