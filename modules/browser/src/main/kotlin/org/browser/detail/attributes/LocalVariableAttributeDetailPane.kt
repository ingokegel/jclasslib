/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.LocalVariableAttribute
import org.gjt.jclasslib.structures.attributes.LocalVariableEntry
import java.util.*

abstract class LocalVariableAttributeDetailPane(services: BrowserServices) : TableDetailPane<LocalVariableAttribute>(LocalVariableAttribute::class.java, services) {

    override fun createTableModel(attribute: LocalVariableAttribute) = AttributeTableModel(attribute.localVariableEntries)

    override val rowHeightFactor: Float
        get() = 2f

    protected abstract val descriptorOrSignatureVerbose: String

    protected inner class AttributeTableModel(rows: Array<LocalVariableEntry>) : ColumnTableModel<LocalVariableEntry>(rows) {

        override fun buildColumns(columns: ArrayList<Column<LocalVariableEntry>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NumberColumn<LocalVariableEntry>("Start PC") {
                    override fun createValue(row: LocalVariableEntry) = row.startPc
                })
                add(object : NumberColumn<LocalVariableEntry>("Length") {
                    override fun createValue(row: LocalVariableEntry) = row.targetLength
                })
                add(object : NumberColumn<LocalVariableEntry>("Index") {
                    override fun createValue(row: LocalVariableEntry) = row.index
                })
                add(object : NamedConstantPoolLinkColumn<LocalVariableEntry>("Name", services, 200) {
                    override fun getConstantPoolIndex(row: LocalVariableEntry) = row.nameIndex
                })
                add(object : NamedConstantPoolLinkColumn<LocalVariableEntry>(descriptorOrSignatureVerbose, services, 200) {
                    override fun getConstantPoolIndex(row: LocalVariableEntry) = row.descriptorOrSignatureIndex
                })
            }
        }
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