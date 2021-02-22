/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.AnnotationData
import org.gjt.jclasslib.structures.attributes.AnnotationDefaultAttribute
import org.gjt.jclasslib.structures.attributes.AnnotationHolder
import org.gjt.jclasslib.structures.elementvalues.*

class RuntimeAnnotationsAttributeDetailPane(services: BrowserServices) : KeyValueDetailPane<AnnotationHolder>(AnnotationHolder::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.annotations.count")) { annotationHolder -> annotationHolder.numberOfAnnotations.toString() }
    }
}

class AnnotationDetailPane(services: BrowserServices) : KeyValueDetailPane<AnnotationData>(AnnotationData::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.type"), AnnotationData::typeIndex)
        addDetail(getString("key.entries.count")) { annotationData -> annotationData.elementValuePairEntries.size.toString() }
    }

    override fun hasInsets() = true
}

class AnnotationDefaultAttributeDetailPane(services: BrowserServices) : KeyValueDetailPane<AnnotationDefaultAttribute>(AnnotationDefaultAttribute::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.value.tag")) { annotationDefaultAttribute -> annotationDefaultAttribute.defaultValue.let { "${it.entryName} <${it.elementValueType.verbose}>" } }
    }
}

class ElementValuePairDetailPane(services: BrowserServices) : KeyValueDetailPane<ElementValuePair>(ElementValuePair::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.element.name"), ElementValuePair::elementNameIndex)
        addDetail(getString("key.value.tag")) { elementValuePair -> "${elementValuePair.elementValue.elementValueType.charTag} <${elementValuePair.elementValue.elementValueType.verbose}>" }
    }

    override fun hasInsets() = true
}

class ArrayElementValueDetailPane(services: BrowserServices) : KeyValueDetailPane<ArrayElementValue>(ArrayElementValue::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.tag")) { arrayElementValue -> arrayElementValue.elementValueType.let { "${it.charTag} <${it.verbose}>" } }
        addDetail(getString("key.values.count")) { arrayElementValue -> arrayElementValue.elementValueEntries.size.toString() }
    }

    override fun hasInsets() = true
}

class GenericElementValueDetailPane(services: BrowserServices) : KeyValueDetailPane<ElementValue>(ElementValue::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.tag")) { elementValue -> "${elementValue.elementValueType.charTag} <${elementValue.elementValueType.verbose}>" }
    }
}

class EnumElementValueEntryDetailPane(services: BrowserServices) : KeyValueDetailPane<EnumElementValue>(EnumElementValue::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.type.name"), EnumElementValue::typeNameIndex)
        addConstantPoolLink(getString("key.const.name"), EnumElementValue::constNameIndex)
    }
}

class ClassElementValueEntryDetailPane(services: BrowserServices) : KeyValueDetailPane<ClassElementValue>(ClassElementValue::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.class.info"), ClassElementValue::classInfoIndex)
    }
}

class ConstElementValueEntryDetailPane(services: BrowserServices) : KeyValueDetailPane<ConstElementValue>(ConstElementValue::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.type.name"), ConstElementValue::constValueIndex)
    }
}


