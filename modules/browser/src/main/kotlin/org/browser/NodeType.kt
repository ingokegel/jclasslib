/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.detail.*

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
    ELEMENTVALUEPAIR {
        override fun createDetailPanel(services: BrowserServices) = ElementValuePairDetailPane(services)
    },
    ELEMENTVALUE {
        override fun createDetailPanel(services: BrowserServices) = ElementValueDetailPane(services)
    },
    ARRAYELEMENTVALUE {
        override fun createDetailPanel(services: BrowserServices) = ArrayElementValueDetailPane(services)
    },
    ATTRIBUTES {
        override fun createDetailPanel(services: BrowserServices) = EmptyDetailPane(services)
    };

    abstract fun createDetailPanel(services: BrowserServices): DetailPane<*>

    companion object {
        fun getByName(name : String?) = values().firstOrNull { it.name == name }
    }
}
