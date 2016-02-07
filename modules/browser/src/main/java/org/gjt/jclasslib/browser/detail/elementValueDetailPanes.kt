/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.constants.ConstantClassInfo
import org.gjt.jclasslib.structures.elementvalues.ClassElementValue
import org.gjt.jclasslib.structures.elementvalues.ConstElementValue
import org.gjt.jclasslib.structures.elementvalues.ElementValue
import org.gjt.jclasslib.structures.elementvalues.EnumElementValue

class GenericElementValueDetailPane(services: BrowserServices) : TypedDetailPane<ElementValue>(ElementValue::class.java, services) {
    override fun addLabels() {
        addDetail("Tag:") { elementValue -> "${elementValue.elementValueType.charTag.toString()} <${elementValue.elementValueType.verbose}>" }
    }
}

class EnumElementValueEntryDetailPane(services: BrowserServices) : TypedDetailPane<EnumElementValue>(EnumElementValue::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Type name:") { elementValue -> elementValue.typeNameIndex }
        addConstantPoolLink("Const name:") { elementValue -> elementValue.constNameIndex }
    }
}

class ClassElementValueEntryDetailPane(services: BrowserServices) : TypedDetailPane<ClassElementValue>(ClassElementValue::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Class info:") { elementValue -> elementValue.classInfoIndex }
    }
}

class ConstElementValueEntryDetailPane(services: BrowserServices) : TypedDetailPane<ConstElementValue>(ConstElementValue::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Type name:") { elementValue -> elementValue.constValueIndex }
    }
}


