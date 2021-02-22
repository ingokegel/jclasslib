/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.KeyValueDetailPane
import org.gjt.jclasslib.structures.attributes.CodeAttribute

class MiscDetailPane(services: BrowserServices) : KeyValueDetailPane<CodeAttribute>(CodeAttribute::class.java, services) {
    init {
        name = getString("code.tab.misc")
    }

    override fun addLabels() {
        addDetail(getString("key.maximum.stack.size")) { attribute -> attribute.maxStack.toString() }
        addDetail(getString("key.maximum.local.variables")) { attribute -> attribute.maxLocals.toString() }
        addDetail(getString("key.code.length")) { attribute -> attribute.code.size.toString() }
    }

    override fun hasInsets() = true
}

