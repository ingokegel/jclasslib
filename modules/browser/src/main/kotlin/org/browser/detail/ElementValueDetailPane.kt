/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.elementvalues.ClassElementValue
import org.gjt.jclasslib.structures.elementvalues.ConstElementValue
import org.gjt.jclasslib.structures.elementvalues.ElementValue
import org.gjt.jclasslib.structures.elementvalues.EnumElementValue

class ElementValueDetailPane(services: BrowserServices) : MultiDetailPane<ElementValue>(ElementValue::class.java, services) {

    override fun addCards() {
        addCard(ConstElementValue::class.java, ConstElementValueEntryDetailPane(services))
        addCard(ClassElementValue::class.java, ClassElementValueEntryDetailPane(services))
        addCard(EnumElementValue::class.java, EnumElementValueEntryDetailPane(services))
    }

    override fun createGenericInfoPane() = GenericElementValueDetailPane(services)
}

