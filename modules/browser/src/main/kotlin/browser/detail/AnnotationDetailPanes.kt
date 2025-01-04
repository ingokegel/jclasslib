/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.constants.DelegateBuilder
import org.gjt.jclasslib.browser.detail.constants.DelegatesEditor
import org.gjt.jclasslib.structures.AnnotationData
import org.gjt.jclasslib.structures.Constant
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
        addEditor { AnnotationEditor() }
    }

    inner class AnnotationEditor : DelegatesEditor<AnnotationData>() {
        override fun DelegateBuilder<AnnotationData>.buildDelegateSpecs() {
            addDelegateSpec {
                services.classFile.getConstantPoolUtf8Entry(it.typeIndex)
            }
        }
    }

    override fun hasInsets() = true
}

class AnnotationDefaultAttributeDetailPane(services: BrowserServices) : KeyValueDetailPane<AnnotationDefaultAttribute>(AnnotationDefaultAttribute::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.value.tag")) { annotationDefaultAttribute -> annotationDefaultAttribute.defaultValue.let { "${it.entryName} <${it.elementValueType.verbose}>" } }
        addEditor { AnnotationDefaultAttributeEditor() }
    }

    class AnnotationDefaultAttributeEditor : DelegatesEditor<AnnotationDefaultAttribute>() {
        override fun DelegateBuilder<AnnotationDefaultAttribute>.buildDelegateSpecs() {
            addEnumSpec(
                    getString("menu.value.tag"),
                    ElementValueType::class.java,
                    { defaultValue.elementValueType },
                    { defaultValue.elementValueType = it }
            )
        }
    }
}

class ElementValuePairDetailPane(services: BrowserServices) : KeyValueDetailPane<ElementValuePair>(ElementValuePair::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.element.name"), ElementValuePair::elementNameIndex)
        addDetail(getString("key.value.tag")) { elementValuePair -> "${elementValuePair.elementValue.elementValueType.charTag} <${elementValuePair.elementValue.elementValueType.verbose}>" }
        addEditor { ElementValuePairEditor() }
    }

    inner class ElementValuePairEditor : DelegatesEditor<ElementValuePair>() {
        override fun DelegateBuilder<ElementValuePair>.buildDelegateSpecs() {
            addDelegateSpec(getString("menu.element.name")) {
                services.classFile.getConstantPoolUtf8Entry(it.elementNameIndex)
            }
        }
    }

    override fun hasInsets() = true
}

abstract class ElementValueDetailPane<T : ElementValue>(elementClass: Class<T>, services: BrowserServices) : KeyValueDetailPane<T>(elementClass, services) {
    override fun hasInsets() = true
}

class ArrayElementValueDetailPane(services: BrowserServices) : ElementValueDetailPane<ArrayElementValue>(ArrayElementValue::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.values.count")) { arrayElementValue -> arrayElementValue.elementValueEntries.size.toString() }
    }
}

class GenericElementValueDetailPane(services: BrowserServices) : ElementValueDetailPane<ElementValue>(ElementValue::class.java, services) {
    override fun addLabels() {
        addDetail(getString("key.tag")) { elementValue -> "${elementValue.elementValueType.charTag} <${elementValue.elementValueType.verbose}>" }
        addEditor { GenericElementValueEditor() }
    }

    class GenericElementValueEditor : DelegatesEditor<ElementValue>() {
        override fun DelegateBuilder<ElementValue>.buildDelegateSpecs() {
            addEnumSpec(getString("menu.value.tag"), ElementValueType::class.java, ElementValue::elementValueType)
        }
    }
}

class EnumElementValueEntryDetailPane(services: BrowserServices) : ElementValueDetailPane<EnumElementValue>(EnumElementValue::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.type.name"), EnumElementValue::typeNameIndex)
        addConstantPoolLink(getString("key.const.name"), EnumElementValue::constNameIndex)
        addEditor { EnumElementValueEntryEditor() }
    }

    inner class EnumElementValueEntryEditor : DelegatesEditor<EnumElementValue>() {
        override fun DelegateBuilder<EnumElementValue>.buildDelegateSpecs() {
            addDelegateSpec(getString("menu.type.name")) {
                services.classFile.getConstantPoolUtf8Entry(it.typeNameIndex)
            }
            addDelegateSpec(getString("menu.const.name")) {
                services.classFile.getConstantPoolUtf8Entry(it.constNameIndex)
            }
        }
    }
}

class ClassElementValueEntryDetailPane(services: BrowserServices) : ElementValueDetailPane<ClassElementValue>(ClassElementValue::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.class.info"), ClassElementValue::classInfoIndex)
        addEditor { ClassElementValueEntryEditor() }
    }

    inner class ClassElementValueEntryEditor : DelegatesEditor<ClassElementValue>() {
        override fun DelegateBuilder<ClassElementValue>.buildDelegateSpecs() {
            addDelegateSpec {
                services.classFile.getConstantPoolUtf8Entry(it.classInfoIndex)
            }
        }
    }
}

class ConstElementValueEntryDetailPane(services: BrowserServices) : ElementValueDetailPane<ConstElementValue>(ConstElementValue::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink(getString("key.const.value"), ConstElementValue::constValueIndex)
        addEditor { ConstElementValueEntryEditor() }
    }

    inner class ConstElementValueEntryEditor : DelegatesEditor<ConstElementValue>() {
        override fun DelegateBuilder<ConstElementValue>.buildDelegateSpecs() {
            addDelegateSpec {
                services.classFile.getConstantPoolEntry(it.constValueIndex, Constant::class.java)
            }
        }
    }
}
