/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.AnnotationData
import org.gjt.jclasslib.structures.attributes.AnnotationDefaultAttribute
import org.gjt.jclasslib.structures.attributes.AnnotationHolder
import org.gjt.jclasslib.structures.elementvalues.*

class RuntimeAnnotationsAttributeDetailPane (services: BrowserServices) : TypedDetailPane<AnnotationHolder>(AnnotationHolder::class.java, services) {
    override fun addLabels() {
        addDetail("Number of annotations:") { annotationHolder -> annotationHolder.numberOfAnnotations.toString()}
    }
}

class AnnotationDetailPane(services: BrowserServices) : TypedDetailPane<AnnotationData>(AnnotationData::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Type:") { annotationData -> annotationData.typeIndex }
        addDetail("Number of entries:") { annotationData -> annotationData.elementValuePairEntries.size.toString() }
    }
}

class AnnotationDefaultAttributeDetailPane(services: BrowserServices) : TypedDetailPane<AnnotationDefaultAttribute>(AnnotationDefaultAttribute::class.java, services) {
    override fun addLabels() {
        addDetail("Value tag:") { annotationDefaultAttribute -> annotationDefaultAttribute.defaultValue.let { "${it.entryName} <${it.elementValueType.verbose}>" } }
    }
}

class ElementValuePairDetailPane(services: BrowserServices) : TypedDetailPane<ElementValuePair>(ElementValuePair::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Element name:") { elementValuePair -> elementValuePair.elementNameIndex }
        addDetail("Value tag:") { elementValuePair -> "${elementValuePair.elementValue.elementValueType.charTag.toString()} <${elementValuePair.elementValue.elementValueType.verbose}>" }
    }
}

class ArrayElementValueDetailPane(services: BrowserServices) : TypedDetailPane<ArrayElementValue>(ArrayElementValue::class.java, services) {
    override fun addLabels() {
        addDetail("Tag:") { arrayElementValue -> arrayElementValue.elementValueType.let { "${it.charTag.toString()} <${it.verbose}>" }}
        addDetail("Number of values:") { arrayElementValue -> arrayElementValue.elementValueEntries.size.toString()}
    }
}

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


