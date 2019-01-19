/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices

class InterfaceDetailPane(services: BrowserServices) : KeyValueDetailPane<Int>(Int::class.javaObjectType, services) {
    override fun addLabels() {
        addConstantPoolLink("Interface:") { index -> index }
    }

    override fun hasInsets() = true
}
