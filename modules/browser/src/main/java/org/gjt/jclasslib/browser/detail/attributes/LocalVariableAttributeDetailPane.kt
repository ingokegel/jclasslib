/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.attributes.LocalVariableAttribute
import org.gjt.jclasslib.structures.attributes.LocalVariableEntry
import java.util.*

abstract class LocalVariableAttributeDetailPane(services: BrowserServices) : ColumnListDetailPane<LocalVariableAttribute>(services) {

    override fun createTableModel(attribute: LocalVariableAttribute) = AttributeTableModel(attribute)

    override val rowHeightFactor: Float
        get() = 2f
    override val attributeClass: Class<LocalVariableAttribute>
        get() = LocalVariableAttribute::class.java

    abstract protected val descriptorOrSignatureVerbose: String

    protected inner class AttributeTableModel(attribute: LocalVariableAttribute) : ColumnTableModel<LocalVariableAttribute>(attribute) {

        override fun buildColumns(columns: ArrayList<Column>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NumberColumn("Start PC") {
                    override fun createValue(rowIndex: Int) = localVariableEntries[rowIndex].startPc
                })
                add(object : NumberColumn("Length") {
                    override fun createValue(rowIndex: Int) = localVariableEntries[rowIndex].length
                })
                add(object : NumberColumn("Index") {
                    override fun createValue(rowIndex: Int) = localVariableEntries[rowIndex].index
                })
                add(object : NamedConstantPoolLinkColumn("Name", services, 200) {
                    override fun getConstantPoolIndex(rowIndex: Int) = localVariableEntries[rowIndex].nameIndex
                })
                add(object : NamedConstantPoolLinkColumn(descriptorOrSignatureVerbose, services, 200) {
                    override fun getConstantPoolIndex(rowIndex: Int) = localVariableEntries[rowIndex].descriptorOrSignatureIndex
                })
            }
        }

        override fun getRowCount(): Int {
            return localVariableEntries.size
        }

        private val localVariableEntries: Array<LocalVariableEntry>
            get() = attribute.localVariableEntries
    }
}

class LocalVariableTableAttributeDetailPane(services: BrowserServices) : LocalVariableAttributeDetailPane(services) {
    override val descriptorOrSignatureVerbose: String
        get() = "Descriptor"
}

class LocalVariableTypeTableAttributeDetailPane(services: BrowserServices) : LocalVariableAttributeDetailPane(services) {
    override val descriptorOrSignatureVerbose: String
        get() = "Signature"
}