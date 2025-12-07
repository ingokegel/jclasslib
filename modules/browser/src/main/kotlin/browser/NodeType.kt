/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.detail.*
import org.gjt.jclasslib.browser.detail.attributes.AttributeDetailPane
import org.gjt.jclasslib.browser.detail.attributes.RecordEntryDetailPane
import org.gjt.jclasslib.browser.detail.constants.ConstantPoolDetailPane
import org.gjt.jclasslib.browser.detail.constants.ConstantPoolEntryDetailPane

enum class NodeType {
    NO_CONTENT {
        override fun createDetailPanel(services: BrowserServices) = EmptyDetailPane(services)
    },
    GENERAL {
        override fun createDetailPanel(services: BrowserServices) = GeneralDetailPane(services)
    },
    CONSTANT_POOL {
        override fun createDetailPanel(services: BrowserServices) = ConstantPoolDetailPane(services)
    },
    CONSTANT_POOL_ENTRY {
        override fun createDetailPanel(services: BrowserServices) = ConstantPoolEntryDetailPane(services)
    },
    INTERFACES {
        override fun createDetailPanel(services: BrowserServices) = EmptyDetailPane(services)
    },
    INTERFACE {
        override fun createDetailPanel(services: BrowserServices) = InterfaceDetailPane(services)
    },
    FIELDS {
        override fun createDetailPanel(services: BrowserServices) =
                ClassMemberContainerDetailPane(services, FixedListWithSignatureDetailPane.SignatureMode.FIELD)
    },
    FIELD {
        override fun createDetailPanel(services: BrowserServices) =
                ClassMemberDetailPane(services, FixedListWithSignatureDetailPane.SignatureMode.FIELD)
    },
    METHODS {
        override fun createDetailPanel(services: BrowserServices) =
                ClassMemberContainerDetailPane(services, FixedListWithSignatureDetailPane.SignatureMode.METHOD)
    },
    METHOD {
        override fun createDetailPanel(services: BrowserServices) =
                ClassMemberDetailPane(services, FixedListWithSignatureDetailPane.SignatureMode.METHOD)
    },
    ATTRIBUTE {
        override fun createDetailPanel(services: BrowserServices) = AttributeDetailPane(services)
    },
    ANNOTATION {
        override fun createDetailPanel(services: BrowserServices) = AnnotationDetailPane(services)
    },
    TYPE_ANNOTATION {
        override fun createDetailPanel(services: BrowserServices) = TypeAnnotationDetailPane(services)
    },
    ELEMENT_VALUE_PAIR {
        override fun createDetailPanel(services: BrowserServices) = ElementValuePairDetailPane(services)
    },
    GENERIC_ELEMENT_VALUE {
        override fun createDetailPanel(services: BrowserServices) = GenericElementValueDetailPane(services)
    },
    CONST_ELEMENT_VALUE {
        override fun createDetailPanel(services: BrowserServices) = ConstElementValueEntryDetailPane(services)
    },
    CLASS_ELEMENT_VALUE {
        override fun createDetailPanel(services: BrowserServices) = ClassElementValueEntryDetailPane(services)
    },
    ENUM_ELEMENTVALUE {
        override fun createDetailPanel(services: BrowserServices) = EnumElementValueEntryDetailPane(services)
    },
    ARRAY_ELEMENT_VALUE {
        override fun createDetailPanel(services: BrowserServices) = ArrayElementValueDetailPane(services)
    },
    RECORD_ENTRY {
        override fun createDetailPanel(services: BrowserServices) = RecordEntryDetailPane(services)
    },
    ATTRIBUTES {
        override fun createDetailPanel(services: BrowserServices) = EmptyDetailPane(services)
    };

    abstract fun createDetailPanel(services: BrowserServices): DetailPane<*>

    companion object {
        fun getByName(name : String?) = entries.firstOrNull { it.name == name }
    }
}
